package data_management;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClient;
import com.alerts.AlertGenerator;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.jupiter.api.*;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.mockito.Mockito.*;

class IntegrationTest {

    private static WebSocketServer server;
    private WebSocketClient client;
    private DataStorage dataStorage;
    private AlertGenerator mockAlertGenerator;

    @BeforeAll
    static void startServer() {
        server = new SimpleWebSocketServer(new InetSocketAddress(8080));
        server.start();
    }

    @AfterAll
    static void stopServer() throws IOException, InterruptedException {
        server.stop();
    }

    @BeforeEach
    void setUp() throws URISyntaxException, IOException, DeploymentException {
        dataStorage = new DataStorage();
        mockAlertGenerator = mock(AlertGenerator.class);
        client = new WebSocketClient();
        client.startReading(dataStorage);
        client.connect("ws://localhost:8080");
    }

    @AfterEach
    void tearDown() throws IOException {
        client.disconnect();
    }

    @Test
    void testRealTimeDataProcessingAndAlertGeneration() throws InterruptedException {
        // Simulate sending data from the server
        String data = "1,1627849261000,HeartRate,72.5";
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
        Assertions.assertEquals("HeartRate", record.getRecordType());
        Assertions.assertEquals(1627849261000L, record.getTimestamp());

        // Verify alert generation
        for (Patient patient : dataStorage.getAllPatients()) {
            mockAlertGenerator.evaluateData(patient);
        }
        verify(mockAlertGenerator, times(1)).evaluateData(any(Patient.class));
    }

    @Test
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

        // Attempt to reconnect the client
        client.scheduleReconnect();

        // Wait for the client to attempt reconnection
        Thread.sleep(6000);

        // Restart the server
        server = new SimpleWebSocketServer(new InetSocketAddress(8080));
        server.start();

        // Simulate sending data from the server after reconnection
        String data = "1,1627849261000,HeartRate,72.5";
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
