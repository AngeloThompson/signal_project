package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * The {@code OxygenSaturationStrategy} class implements the {@link AlertStrategy} interface.
 * This strategy is responsible for monitoring oxygen saturation levels in patient records
 * and generating alerts when specific conditions are met.
 */
public class OxygenSaturationStrategy implements AlertStrategy {

    private AlertFactory factory;

    /**
     * Checks the patient's records for oxygen saturation alerts.
     *
     * This method iterates through the provided patient records to detect
     * if the oxygen saturation level is below a critical threshold or if
     * there is a rapid drop in oxygen saturation. If any such condition is met,
     * an appropriate alert is generated using the {@link BloodOxygenAlertFactory}.
     *
     * @param patient the patient whose records are being evaluated
     * @param records the list of patient records to evaluate
     * @return an {@link Alert} if any alert condition is met, otherwise {@code null}
     */
    @Override
    public Alert checkAlert(Patient patient, List<PatientRecord> records) {
        double prevOxygenSaturation = 0;
        long prevTimestamp = 0;
        factory = new BloodOxygenAlertFactory();

        for (PatientRecord record : records) {
            String recordType = record.getRecordType();
            double measurementValue = record.getMeasurementValue();
            long timestamp = record.getTimestamp();

            if ("Saturation".equals(recordType)) {
                if (measurementValue < 92) {
                    return factory.createAlert("" + record.getPatientId(), "LowSaturation", record.getTimestamp());
                }

                double drop = prevOxygenSaturation - measurementValue;
                if (prevTimestamp != 0 && timestamp - prevTimestamp <= 600000 && drop >= 5) {
                    return factory.createAlert("" + record.getPatientId(), "rapidSaturationDrop", record.getTimestamp());
                }

                prevOxygenSaturation = measurementValue;
                prevTimestamp = timestamp;
            }
        }
        return null;
    }
}
