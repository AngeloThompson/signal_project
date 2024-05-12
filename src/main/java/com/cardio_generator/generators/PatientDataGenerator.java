package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This interface represents a generator for patient data.
 * Implementations of this interface are responsible for generating specific
 *     health data for patients.
 */
public interface PatientDataGenerator {

    /**
     * Generates health data for a specific patient.
     * 
     * @param patientId      The ID of the patient as an integer used to generate
     *     patient specific data.
     * @param outputStrategy The output strategy as a OutputStrategy object, used to
     *     output the generated data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
