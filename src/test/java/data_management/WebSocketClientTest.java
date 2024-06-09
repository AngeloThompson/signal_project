package data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.data_management.DataStorage;
import com.data_management.WebSocketClient;

import javax.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebSocketClientTest {

    private WebSocketClient client;
    private DataStorage mockDataStorage;
    private WebSocketContainer mockContainer;

    @BeforeEach
    void setUp() {
        mockContainer = mock(WebSocketContainer.class);
        client = new WebSocketClient(mockContainer);
        mockDataStorage = Mockito.mock(DataStorage.class);
        DataStorage.setInstance(mockDataStorage); // Set the mock instance
        client.startReading(mockDataStorage);
    }

    @Test
    void testConnect() throws URISyntaxException, IOException, DeploymentException {
        // Mock the connectToServer method to do nothing
        doAnswer(invocation -> null).when(mockContainer).connectToServer(any(Object.class), any(URI.class));

        // Attempt to connect using the mocked container
        client.connect("ws://localhost:8080");

        // Verify the connectToServer method was called once
        verify(mockContainer, times(1)).connectToServer(any(Object.class), any(URI.class));
    }
    

    @Test
    void testHandleValidData() {
        String validData = "1,1627849261000,HeartRate,72.5";
        client.handleData(validData, mockDataStorage);
        verify(mockDataStorage, times(1)).addPatientData(1, 72.5, "HeartRate", 1627849261000L);
    }

    @Test
    void testHandleInvalidData() {
        String invalidData = "invalid,data,format";
        client.handleData(invalidData, mockDataStorage);
        verify(mockDataStorage, times(0)).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    @Test
    void testOnMessage() {
        String message = "1,1627849261000,HeartRate,72.5";
        client.onMessage(message);
        verify(mockDataStorage, times(1)).addPatientData(1, 72.5, "HeartRate", 1627849261000L);
    }

    @Test
    void testReconnectOnClose() throws IOException, URISyntaxException {
        WebSocketClient spyClient = Mockito.spy(client);
        spyClient.onClose(null, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Normal closure"));
        verify(spyClient, times(1)).scheduleReconnect();
    }

    @Test
    void testReconnectOnError() throws IOException, URISyntaxException {
        WebSocketClient spyClient = Mockito.spy(client);
        spyClient.onError(null, new Exception("Test exception"));
        verify(spyClient, times(1)).scheduleReconnect();
    }
}

