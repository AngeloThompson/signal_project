package com.cardio_generator.outputs;

// Section 3.3.3, imports ordered based on ASCII.
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.PrintWriter;
import java.nio.file.StandardOpenOption;

// Section 5.2.2, class name changed to UpperCamelCase.
// Section 4.2, all indentations changed to +2 spaces.
/**
 * This class implements the OutputStrategy interface to output health data to a file.
 * It creates separate files for each different type of health data.
 */
public class FileOutputStrategy implements OutputStrategy {

  // Section 5.2.5, field name changed to camelCase.
  private String baseDirectory;

  // Section 4.6.2, horizontol whitespace added before and after "<>" and after ")".
  public final ConcurrentHashMap <String, String> file_map = new ConcurrentHashMap <> () ;

  /**
   * Constructs a new FileOutputStrategy with the specified base directory.
   * 
   * @param baseDirectory The base directory where output files will be stored as a String.
   */
  public FileOutputStrategy(String baseDirectory) {

    this.baseDirectory = baseDirectory;
  }
  // Comments added here to reduce clutter.
  // Section 4.6.2, space added after all ")" in this method.
  // Section 7.2, comment punctuation added to all comments in this method.
  /**
   * Outputs health data corresponding with given patient IDs, timestamp,
   *     and health data label to a file.
   * Creates a new file containing a specific type of health data if not already present. 
   * 
   * @param patientId The ID of the patient as an integer.
   * @param timestamp The timestamp of the health data as a long.
   * @param label     The type of the health data as a String.
   * @param data      The actual health data to be written to the file as a String.
   */
  @Override
  public void output(int patientId, long timestamp, String label, String data) {
    try {
      // Create the directory.
      Files.createDirectories(Paths.get(baseDirectory) ) ;
    } catch (IOException e) {
      System.err.println("Error creating base directory: " + e.getMessage() ) ;
      return;
    }
    // Set the FilePath variable.
    // Section 5.2.7 and Section 4.4, local variable changed to camelCase and line wrapped.
    String filePath = file_map.computeIfAbsent(
        label, k -> Paths.get(baseDirectory, label + ".txt") .toString() ) ;

    // Write the data to the file.
    // Section 4.4, lines in this try-catch block were linewrapped to not exceed 100 characters.
    try (PrintWriter out = new PrintWriter(
        Files.newBufferedWriter(
            Paths.get(filePath) , StandardOpenOption.CREATE, StandardOpenOption.APPEND) ) ) {
                
      out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
          patientId, timestamp, label, data) ;
    } catch (Exception e) {
      System.err.println("Error writing to file " + filePath + ": " + e.getMessage() ) ;
    }
  }
}