package com.data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Implementation of the {@link DataReader} interface to read data from an output file
 * generated using the --output file:<output_dir> argument.
 *
 * <p>This class reads data from the specified directory and passes it to a {@link DataStorage}
 * for further processing.
 */
public class OutputFileDataReader implements DataReader {

    private String outputDir;
    private String label;

    /**
     * Constructs an {@code OutputFileDataReader} with the specified output directory.
     *
     * @param outputDir the directory where the output file is located
     * @param label The type of the health data as a String.
     */
    public OutputFileDataReader(String outputDir, String label) {
        this.outputDir = outputDir;
        this.label = label;
    }

    /**
     * Reads data from the output file and stores it in the provided {@link DataStorage}.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if an I/O error occurs while reading the data
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        // Construct the path to the output file
        Path outputFile = Paths.get(outputDir, label+".txt");

        // Check if the file exists
        if (!Files.exists(outputFile)) {
            throw new IOException("Output file does not exist.");
        }

        // Read data from the file and pass it to the data storage
        try (Scanner scanner = new Scanner(outputFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Assuming each line contains a piece of data in the specified format
                parseAndStoreData(line, dataStorage);
            }
        }
    }

    /**
     * Parses a line of data in the specified format and stores it in the provided {@link DataStorage}.
     *
     * @param line        the line of data to parse
     * @param dataStorage the storage where data will be stored
     */
    private void parseAndStoreData(String line, DataStorage dataStorage) {
        // Parse data from the line
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter(", ");
        int patientId = lineScanner.nextInt();
        long timestamp = lineScanner.nextLong();
        String label = lineScanner.next();
        String dataString = lineScanner.next();
        double data = Double.parseDouble(dataString);

        // Store data using DataStorage
        dataStorage.addPatientData(patientId, data, label, timestamp);

        // Close the line scanner
        lineScanner.close();
    }
}
