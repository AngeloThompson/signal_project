package com.cardio_generator.outputs;

/**
 * This interface represents a strategy for outputting health data.
 */
public interface OutputStrategy {

    /**
     * Outputs health data for a specific patient.
     * 
     * @param patientId The integer ID of the patient for whom the data is being outputted.
     * @param timestamp The timestamp of the data as a long.
     * @param label     The type of the health data as a String.
     * @param data      The health data as a String.
     */
    void output(int patientId, long timestamp, String label, String data);
}
