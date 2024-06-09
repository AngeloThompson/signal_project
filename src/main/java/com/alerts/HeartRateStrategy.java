package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HeartRateStrategy implements AlertStrategy {

    private AlertFactory factory;
    private static final int SLIDING_WINDOW_SIZE = 10; // Size of the sliding window for averaging ECG values
    private static final double PEAK_THRESHOLD_MULTIPLIER = 1.5; // Multiplier to determine significant peaks
    
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

