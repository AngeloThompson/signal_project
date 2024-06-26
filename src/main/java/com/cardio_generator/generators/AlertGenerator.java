package com.cardio_generator.generators;

// Changed the order of the import statements and removed the line break (section 3.3.3).
import com.cardio_generator.outputs.OutputStrategy;
import java.util.Random;

// Reduced the indentation to 2 spaces for all the following code (section 4.2).

/**
 * The AlerGenerator class generates alerts for patients and
 *     sends the alert data to an output strategy.
 * 
 * This class implements the PatientDataGenerator interface to generate alerts for patients.
 * 
 * Alerts can either be triggered or resolved, and the generator simulates the occurrence 
 *     and resolution of alerts based on predefined probabilities.
 * 
 * This class is intended for use within a medical monitoring system where
 *     alerts need to be generated and processed for multiple patients.
 */
public class AlertGenerator implements PatientDataGenerator {

  // Added a single space after each ")" below (section 4.6.2).
  public static final Random randomGenerator = new Random() ;

  // Added a vertical whitespace in between constructors (section 4.6.1).
  // Changed the field name to camelCase (section 5.2.5).
  private boolean[] alertStates; // false = resolved, true = pressed.

    /**
     * Constructs an AlertGenerator with the specified number of patients.
     * 
     * @param patientCount The total number of patients for whom alerts need to be generated
     *     as an integer.
     */
    public AlertGenerator(int patientCount) {
      alertStates = new boolean[patientCount + 1];
    }

    // Added a single space after each ")" in the below method (section 4.6.2).
    // Added punctuation to all comments (section 7.2).
    /**
     * Generates alerts for a specific patient and sends the alert data to an output strategy.
     * 
     * @param patientId The ID of the patient for whom the alert data is being generated.
     *     It specifies the unique identifier of the patient as an integer.
     * 
     * @param outputStrategy The strategy used to output the generated alert data which specifies
     *     the output strategy to which the generated alert data will be sent.
     * 
     * @throws None is explicitly thrown but any exceptions occurring during
     *     the generation process are caught and handled within the method.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
      try {
        if (alertStates[patientId]) {
          if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve.
            alertStates[patientId] = false;
            // Output the alert.
            outputStrategy.output(patientId, System.currentTimeMillis() , "Alert", "resolved") ;
            }
            } else {
              double Lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency.
              double p = -Math.expm1(-Lambda) ; // Probability of at least one alert in the period.
              boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                  alertStates[patientId] = true;
                  // Output the alert.
                  // Line-wrapped since it was exceeding the 100 character column limit (sections 4.4 - 4.5.1).
                  outputStrategy.output(
                      patientId, System.currentTimeMillis() , "Alert", "triggered") ;      
                }
            }
        } catch (Exception e) {
          // Line-wrapped since it was exceeding the 100 character column limit (sections4.4 - 4.5.1).
          System.err.println(
              "An error occurred while generating alert data for patient " + patientId) ;
          e.printStackTrace() ;
        }
    }
}