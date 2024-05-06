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
public class FileOutputStrategy implements OutputStrategy {

  // Section 5.2.5, field name changed to camelCase.
  private String baseDirectory;

  // Section 4.6.2, horizontol whitespace added before and after "<>" and after ")".
  public final ConcurrentHashMap <String, String> file_map = new ConcurrentHashMap <> () ;

  public FileOutputStrategy(String baseDirectory) {

    this.baseDirectory = baseDirectory;
  }
  // Comments added here to reduce clutter.
  // Section 4.6.2, space added after all ")" in this method.
  // Section 7.2, comment punctuation added to all comments in this method.
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