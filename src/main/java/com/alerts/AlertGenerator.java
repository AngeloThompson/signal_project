package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alerts;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
        // 20 minute time window.
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(),(long)System.currentTimeMillis()-1200000,(long)(System.currentTimeMillis()));
        int systolicTrend=0;
        double prevSystolic=0;
        int diastolicTrend =0;
        double prevDiastolic =0;
        double prevOxygenSaturation = 0;
        long prevTimestamp = 0;
        double prevBPM=0;
        boolean lowSystolic=false; // adjust
        boolean lowSat=false;

        for (PatientRecord record:records){
            String recordType = record.getRecordType();
            double measurementValue = record.getMeasurementValue();
            long timestamp = record.getTimestamp();
            int patientId = record.getPatientId();

            switch (recordType){

            // blood pressure checks
            case "SystolicPressure":{
                // systolic pressure check.
                if(systolicCriticalCheck(measurementValue)>=0){
                    // Systolic pressure low check
                    if(systolicCriticalCheck(measurementValue)==0){
                        lowSystolic=true; 
                        triggerAlert(new Alert(""+record.getPatientId(), "LowSystolicPressure", record.getTimestamp()));
                    }
                    else{
                        lowSystolic=false;
                        triggerAlert(new Alert(""+record.getPatientId(), "HighSystolicPressure", record.getTimestamp()));
                    }
                }
                // if systolic pressure trend occurs.
                int increment = systolicTrend(prevSystolic,record.getMeasurementValue());
                if (increment!=0){
                    // if the trend is in the oppositie direction of previous trend.
                    if((systolicTrend>0&&increment==-1)||(systolicTrend<0&&increment==1)){
                        systolicTrend=0;
                    }
                    systolicTrend += increment;
                    // if positive trend.
                    if(systolicTrend==3){
                        systolicTrend=0;
                        triggerAlert(new Alert(""+record.getPatientId(), "SystolicPressureIncreaseTrend", record.getTimestamp())); 
                    }
                    // if negetaive trend.
                    else if(systolicTrend== -3){
                        systolicTrend=0;
                        triggerAlert(new Alert(""+record.getPatientId(), "SystolicPressureDecreaseTrend", record.getTimestamp())); 
                    }
                }
                else {systolicTrend=0;}
                prevSystolic = measurementValue;
                break;
            }

            // diastolic pressure checks.
            case "DiastolicPressure":{
                // diastolic pressure high.
                if(diastolicCriticalCheck(measurementValue)>=0){
                    Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                    triggerAlert(alert); 
                }
                // if systolic pressure trend occurs.
                int increment = diastolicTrend(prevSystolic,record.getMeasurementValue());
                if (increment!=0){
                    // if the trend is in the oppositie direction of previous trend.
                    if((diastolicTrend>0&&increment==-1)||(diastolicTrend<0&&increment==1)){
                        diastolicTrend=0;
                    }
                    diastolicTrend += increment;
                    // if positive trend.
                    if(diastolicTrend==3){
                        systolicTrend=0;
                        triggerAlert(new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp())); 
                    }
                    // if negetaive trend.
                    else if(diastolicTrend== -3){
                        systolicTrend=0;
                        triggerAlert(new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp())); 
                    }
                }
                else {systolicTrend=0;}
                prevDiastolic = measurementValue;
                break;
            }
            // blood O2 saturation 
            case "Saturation":{
                // Low Saturation Alert
                if (measurementValue < 92) {
                    lowSat=true;
                    triggerAlert(new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp()));
                }
                else{lowSat=false;}
                // Rapid Drop Alert
                if (prevTimestamp != 0 && timestamp - prevTimestamp <= 600000) { // Within 10 minutes interval
                    double drop = prevOxygenSaturation - measurementValue;
                    if (drop >= 5) {
                        triggerAlert(new Alert(""+record.getPatientId(), "Rapid Drop Alert", record.getTimestamp()));
                    }
                }
                prevOxygenSaturation = measurementValue;
                prevTimestamp = timestamp;
                break;
            }
            // ECG check.
            case "ECG":{
                // Abnormal heart rate check.
                if(measurementValue <50 ||measurementValue >100){
                    triggerAlert(new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp())); 
                }
                // Irregular beat pattern check.
                if (Math.abs(prevBPM - measurementValue) >= 30) { 
                    triggerAlert(new Alert(""+record.getPatientId(), "Irregular_BPM", record.getTimestamp()));
                }
                prevBPM = measurementValue;
                break;
            }
            }
            // Hypotensive hypoxia check 
            if (lowSystolic&&lowSat){
                triggerAlert(new Alert(""+record.getPatientId(), "HypotensiveHypoxemia", record.getTimestamp()));
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        // Logs the alert generated.
        alerts.add(alert);
    }
    // Added methods.
    public int systolicTrend(double systolic1, double systolic2) {
        // Check for high trend
        double systolicTemp=systolic1+10;
        if (systolic2>systolicTemp){return 1;}
        // Check for low trend
        systolicTemp=systolic1-10;
        if (systolic2<systolicTemp){return -1;}
        return 0;
    }
    public int diastolicTrend(double diastolic1, double diastolic2) {
        // Check for high trend
        double systolicTemp=diastolic1+10;
        if (diastolic2>systolicTemp){return 1;}
        // Check for low trend
        systolicTemp=diastolic1-10;
        if (diastolic2<systolicTemp){return -1;}
        return 0;
    }
    
    public int systolicCriticalCheck(double systolic) {
        // Check for Critical Threshold Alert
        if (systolic > 180) {return 1;}
        else if (systolic < 90) {return 0;}
        else return 0;
    }
    public int diastolicCriticalCheck(double diastolic) {
        // Check for Critical Threshold Alert
        if (diastolic > 120) {return 1;}
        else if (diastolic < 60) {return 0;}
        else return 0;
    }

    // Added methods.
}
