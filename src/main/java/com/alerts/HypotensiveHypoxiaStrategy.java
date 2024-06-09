package com.alerts;

import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HypotensiveHypoxiaStrategy implements AlertStrategy {

    private AlertFactory factory;

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
    private int systolicCriticalCheck(double systolic) {
        if (systolic > 180) { return 1; }
        else if (systolic < 90) { return 0; }
        else { return -1; }
    }
}
