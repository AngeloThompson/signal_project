package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {

    private AlertFactory factory;

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
