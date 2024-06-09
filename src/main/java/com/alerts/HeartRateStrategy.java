package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The {@code HeartRateStrategy} class implements the {@link AlertStrategy} interface.
 * This strategy is responsible for monitoring patient records for significant ECG peaks
 * and generating alerts when specific conditions are met.
 */
public class HeartRateStrategy implements AlertStrategy {

    private AlertFactory factory;
    private static final int SLIDING_WINDOW_SIZE = 10; // Size of the sliding window for averaging ECG values
    private static final double PEAK_THRESHOLD_MULTIPLIER = 1.5; // Multiplier to determine significant peaks
    
    /**
     * Checks the patient's records for significant ECG peaks.
     *
     * This method iterates through the provided patient records to detect
     * if there are significant peaks in the ECG values using a sliding window
     * average approach. If a significant peak is detected, an appropriate alert
     * is generated using the {@link ECGAlertFactory}.
     *
     * @param patient the patient whose records are being evaluated
     * @param records the list of patient records to evaluate
     * @return an {@link Alert} if any alert condition is met, otherwise {@code null}
     */
    @Override
    public Alert checkAlert(Patient patient, List<PatientRecord> records) {

        Queue<Double> ecgWindow = new LinkedList<>();
        double ecgSum = 0;
        factory = new ECGAlertFactory();

        for (PatientRecord record : records) {
            String recordType = record.getRecordType();
            double measurementValue = record.getMeasurementValue();
            long timestamp = record.getTimestamp();

            if ("ECG".equals(recordType)) {

                // Calculate the average ECG value over the sliding window
                double ecgAverage = ecgSum / ecgWindow.size();

                // Check for significant peak
                if (Math.abs(measurementValue) > ecgAverage * PEAK_THRESHOLD_MULTIPLIER) {
                    return factory.createAlert("" + record.getPatientId(), "SignificantEcgPeak", timestamp);
                }

                // Add the current ECG value to the sliding window
                if (ecgWindow.size() == SLIDING_WINDOW_SIZE) {
                    ecgSum -= ecgWindow.poll();
                }
                
                ecgWindow.add(measurementValue);
                ecgSum += measurementValue;
            }
        }
        return null;
    }
}

