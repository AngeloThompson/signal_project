package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code HypotensiveHypoxiaStrategy} class implements the {@link AlertStrategy} interface.
 * This strategy is responsible for monitoring patient records for signs of hypotensive hypoxemia
 * and generating alerts when specific conditions are met.
 */
public class HypotensiveHypoxiaStrategy implements AlertStrategy {

    private AlertFactory factory;

    /**
     * Checks the patient's records for hypotensive hypoxemia alerts.
     *
     * This method iterates through the provided patient records to detect
     * if both the systolic blood pressure is below a critical threshold and
     * if the oxygen saturation level is low. If both conditions are met,
     * an appropriate alert is generated using the {@link HypotensiveHypoxiaFactory}.
     *
     * @param patient the patient whose records are being evaluated
     * @param records the list of patient records to evaluate
     * @return an {@link Alert} if any alert condition is met, otherwise {@code null}
     */
    @Override
    public Alert checkAlert(Patient patient, List<PatientRecord> records) {
        boolean lowSystolic=false; 
        boolean lowSat=false;
        factory = new HypotensiveHypoxiaFactory();

        for (PatientRecord record : records) {
            String recordType = record.getRecordType();
            double measurementValue = record.getMeasurementValue();
            long timestamp = record.getTimestamp();

            if ("Saturation".equals(recordType)) {
                if (measurementValue < 92) {
                    lowSat = true;
                } else {lowSat = false;}
            }
            else if ("SystolicPressure".equals(recordType)){
                if(systolicCriticalCheck(measurementValue)==0){
                    // Systolic pressure low check
                    lowSystolic = true;
                } else {lowSystolic=false;}
            }
            if (lowSat && lowSystolic){
                return factory.createAlert(""+patient.getPatientId(),"HypotensiveHypoxemia",timestamp);
            }
        }
        return null;
    }

    /**
     * Checks the systolic blood pressure for critical thresholds.
     *
     * @param systolic the systolic blood pressure value to check
     * @return 1 if the systolic pressure is critically high, 0 if it is critically low, -1 otherwise
     */
    private int systolicCriticalCheck(double systolic) {
        if (systolic > 180) { return 1; }
        else if (systolic < 90) { return 0; }
        else { return -1; }
    }
}
