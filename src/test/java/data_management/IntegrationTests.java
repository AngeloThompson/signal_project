package data_management;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClient;
import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.BloodPressureStrategy;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.jupiter.api.*;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class IntegrationTests {

    private static WebSocketServer server;
    private WebSocketClient client;
    private DataStorage dataStorage;
    private AlertGenerator mockAlertGenerator;

    @BeforeAll
    static void startServer() {
        server = new SimpleWebSocketServer(new InetSocketAddress(8080));
        server.start();
        System.out.println("Server started successfully");
    }

    @AfterAll
    static void stopServer() throws IOException, InterruptedException {
        server.stop();
        System.out.println("Server stopped successfully");
    }

    @BeforeEach
    void setUp() throws URISyntaxException, IOException, DeploymentException {
        dataStorage = mock(DataStorage.class);
        DataStorage.setInstance(dataStorage); // Set the mock instance
        mockAlertGenerator = new AlertGenerator(DataStorage.getInstance());
        client = new WebSocketClient();
        client.startReading(DataStorage.getInstance());
        client.connect("ws://localhost:8080");

        // Mocking addPatientData to store and retrieve the expected records
        doAnswer(invocation -> {
            int patientId = invocation.getArgument(0);
            double value = invocation.getArgument(1);
            String recordType = invocation.getArgument(2);
            long timestamp = invocation.getArgument(3);
            List<PatientRecord> records = new ArrayList<>();
            records.add(new PatientRecord(patientId, value, recordType, timestamp));
            when(dataStorage.getRecords(eq(patientId), anyLong(), anyLong())).thenReturn(records);
            return null;
        }).when(dataStorage).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());

        when(dataStorage.getAllPatients()).thenReturn(Collections.singletonList(new Patient(1)));
    }

    @AfterEach
    void tearDown() throws IOException {
        client.disconnect();
    }

    @Test
    @DisplayName("Real Time Data processing and AlertGeneration")
    void testRealTimeDataProcessingAndAlertGeneration() throws InterruptedException {
        mockAlertGenerator.setAlertStrategy(new BloodPressureStrategy());
        // Simulate sending data from the server
        String data = "1,1627849261000,SystolicPressure,72.5";
        for (WebSocket conn : server.getConnections()) {
            conn.send(data);
        }

        // Wait for the client to process the message
        Thread.sleep(1000);

        // Verify data storage
        List<PatientRecord> records = dataStorage.getRecords(1, 1627849260000L, 1627849262000L);
        Assertions.assertEquals(1, records.size());
        PatientRecord record = records.get(0);
        Assertions.assertEquals(1, record.getPatientId());
        Assertions.assertEquals(72.5, record.getMeasurementValue());
        Assertions.assertEquals("SystolicPressure", record.getRecordType());
        Assertions.assertEquals(1627849261000L, record.getTimestamp());

        // Verify alert generation
        for (Patient patient : dataStorage.getAllPatients()) {
            mockAlertGenerator.evaluateData(patient);
        }
        Alert alert = mockAlertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolicPressure", alert.getCondition());
    }

    @Test
    @DisplayName("Handle Corrupted Data")
    void testHandleCorruptedData() throws InterruptedException {
        // Simulate sending corrupted data from the server
        String corruptedData = "corrupted,data";
        for (WebSocket conn : server.getConnections()) {
            conn.send(corruptedData);
        }

        // Wait for the client to process the message
        Thread.sleep(1000);

        // Verify that no data is added to the storage
        List<PatientRecord> records = dataStorage.getRecords(1, 1627849260000L, 1627849262000L);
        Assertions.assertTrue(records.isEmpty());
    }

    @Test
    void testHandleConnectionLoss() throws InterruptedException, IOException, URISyntaxException {
        // Disconnect the server to simulate connection loss
        server.stop();
        Thread.sleep(1000);

        // Attempt to reconnect the client after 5 seconds.
        client.scheduleReconnect();

        // Wait for the client to attempt reconnection
        // Restart the server
        server = new SimpleWebSocketServer(new InetSocketAddress(8080));
        server.start();
        Thread.sleep(6000);

        // Simulate sending data from the server after reconnection
        String data = "1,1627849261000,HeartRate,72.5";
        for (WebSocket conn : server.getConnections()) {
            conn.send(data);
        }

        // Wait for the client to process the message
        Thread.sleep(2000);

        // Verify data storage
        List<PatientRecord> records = dataStorage.getRecords(1, 1627849260000L, 1627849262000L);
        Assertions.assertEquals(1, records.size());
        PatientRecord record = records.get(0);
        Assertions.assertEquals(1, record.getPatientId());
        Assertions.assertEquals(72.5, record.getMeasurementValue());
        Assertions.assertEquals("HeartRate", record.getRecordType());
        Assertions.assertEquals(1627849261000L, record.getTimestamp());
    }

    // Simple WebSocket server for testing
    private static class SimpleWebSocketServer extends WebSocketServer {

        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Echo the message back to the client
            conn.send(message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
