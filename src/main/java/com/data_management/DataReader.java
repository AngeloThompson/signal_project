// package com.data_management;

// import java.io.IOException;

// public interface DataReader {
//     /**
//      * Reads data from a specified source and stores it in the data storage.
//      * 
//      * @param dataStorage the storage where data will be stored
//      * @throws IOException if there is an error reading the data
//      */
//     void readData(DataStorage dataStorage) throws IOException;
// }
package com.data_management;

import java.io.IOException;
import java.net.URISyntaxException;

public interface DataReader {
    /**
     * Connects to a WebSocket server to start receiving real-time data.
     * 
     * @param serverUri the URI of the WebSocket server
     * @throws IOException if there is an error during connection
     * @throws URISyntaxException if the server URI is invalid
     */
    void connect(String serverUri) throws IOException, URISyntaxException;

    /**
     * Disconnects from the WebSocket server.
     * 
     * @throws IOException if there is an error during disconnection
     */
    void disconnect() throws IOException;

    /**
     * Handles the incoming data from the WebSocket server and stores it in the data storage.
     * 
     * @param data the data received from the WebSocket server
     * @param dataStorage the storage where data will be stored
     */
    void handleData(String data, DataStorage dataStorage);

    /**
     * Starts the real-time data reading process.
     * 
     * @param dataStorage the storage where data will be stored
     */
    void startReading(DataStorage dataStorage);
}
