package com.data_management;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * WebSocketClient class implements the DataReader interface to handle WebSocket communication.
 * It connects to a WebSocket server, handles incoming messages, and stores them in DataStorage.
 */
@ClientEndpoint
public class WebSocketClient implements DataReader {
    private Session session;
    private DataStorage dataStorage;
    private URI serverUri;
    private Timer reconnectTimer;
    private WebSocketContainer container;
    /**
     * Default constructor which uses the default WebSocketContainer.
     */
    public WebSocketClient() {
        this(ContainerProvider.getWebSocketContainer());
    }

    /**
     * Constructor that accepts a WebSocketContainer for testing.
     *
     * @param container the WebSocketContainer to use for connecting
     */
    public WebSocketClient(WebSocketContainer container) {
        this.container = container;
    }

    /**
     * Connects to the WebSocket server using the provided URI.
     *
     * @param serverUri the URI of the WebSocket server
     * @throws IOException if there is an error during connection
     * @throws URISyntaxException if the server URI is invalid
     */
    @Override
    public void connect(String serverUri) throws IOException, URISyntaxException {
        this.serverUri = new URI(serverUri);
        connectToServer();
    }

    /**
     * Connects to the WebSocket server.
     *
     * @throws IOException if there is an error during connection
     */
    private void connectToServer() throws IOException {
        try {
            container.connectToServer(this, serverUri);
        } catch (DeploymentException e) {
            throw new IOException("Error connecting to WebSocket server", e);
        }
    }

    /**
     * Disconnects from the WebSocket server.
     *
     * @throws IOException if there is an error during disconnection
     */
    @Override
    public void disconnect() throws IOException {
        if (session != null && session.isOpen()) {
            session.close();
        }
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
    }

    /**
     * Handles incoming data by parsing it and storing it in the DataStorage.
     *
     * @param data the data received from the WebSocket server
     * @param dataStorage the storage where data will be stored
     */
    @Override
    public void handleData(String data, DataStorage dataStorage) {
        PatientRecord record = parseData(data);
        if (record != null) {
            int patientId = record.getPatientId();
            double measurementValue = record.getMeasurementValue();
            long timestamp = record.getTimestamp();
            String recordType = record.getRecordType();
            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            System.out.println("Added data to storage: " + record);
        } else {
            System.err.println("Received corrupted data: " + data);
        }
    }

    /**
     * Starts the real-time data reading process.
     *
     * @param dataStorage the storage where data will be stored
     */
    @Override
    public void startReading(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Parses the incoming data string into a PatientRecord object.
     *
     * @param data the data string to be parsed
     * @return a PatientRecord object if parsing is successful, null otherwise
     */
    private PatientRecord parseData(String data) {
        try {
            String[] parts = data.split(",");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                String label = parts[2];
                double value = Double.parseDouble(parts[3]);
                return new PatientRecord(patientId, value, label, timestamp);
            } else {
                // For corrupted data.
                return null; 
            }
        } catch (Exception e) {
            e.printStackTrace();
            // For corrupted data.
            return null;
        }
    }

    /**
     * Called when the WebSocket connection is opened.
     *
     * @param session the WebSocket session
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to WebSocket server");
        this.session = session;
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
    }

    /**
     * Called when a message is received from the WebSocket server.
     *
     * @param message the received message
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        handleData(message, dataStorage);
    }

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param session the WebSocket session
     * @param closeReason the reason for closing the connection
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from WebSocket server: " + closeReason);
        scheduleReconnect();
    }

    /**
     * Called when an error occurs during WebSocket communication.
     *
     * @param session the WebSocket session
     * @param throwable the error that occurred
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on WebSocket connection: " + throwable.getMessage());
        throwable.printStackTrace();
        scheduleReconnect();
    }

    /**
     * Schedules a reconnection attempt after a delay.
     */
    public void scheduleReconnect() {
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
        reconnectTimer = new Timer(true);
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Attempting to reconnect to WebSocket server");
                    connectToServer();
                } catch (IOException e) {
                    System.err.println("Reconnection attempt failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 5000); // Attempt to reconnect after 5 seconds
    }
}
