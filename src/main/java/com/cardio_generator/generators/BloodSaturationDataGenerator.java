package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * The BloodSaturationGenerator class is responsible for
 *     generating simulated blood saturation data for multiple patients.
 * 
 * This class implements the PatientDataGenerator interface.
 */

public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /**
     * Initializes the lastSaturationValues array with baseline saturation values for each patient.
     * 
     * @param patientCount The number of patients for whom blood saturation
     *     data needs to be generated.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates simulated blood saturation data for a specific patient 
     *     and sends the data to an output strategy.
     * 
     * @param patientId The ID of the patient for whom the blood saturation data
     *     is being generated represented as an integer.
     * 
     * @param outputStrategy Specifies the output strategy to which 
     *     the generated blood saturation data will be sent.
     * 
     * @throws None explicitly thrown but any exceptions occurring during the generation process
     *     are caught and handled within the method.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
