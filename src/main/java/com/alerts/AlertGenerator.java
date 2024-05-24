package com.alerts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    private static final int SLIDING_WINDOW_SIZE = 10; // Size of the sliding window for averaging ECG values
    private static final double PEAK_THRESHOLD_MULTIPLIER = 1.5; // Multiplier to determine significant peaks

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
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(),System.currentTimeMillis()-1200000,(System.currentTimeMillis()));
        int systolicTrend=0;
        double prevSystolic=0;
        int diastolicTrend =0;
        double prevDiastolic =0;
        double prevOxygenSaturation = 0;
        long prevTimestamp = 0;
        boolean lowSystolic=false; 
        boolean lowSat=false;
        

        Queue<Double> ecgWindow = new LinkedList<>();
        double ecgSum = 0;

        for (PatientRecord record:records){
            String recordType = record.getRecordType();
            double measurementValue = record.getMeasurementValue();
            long timestamp = record.getTimestamp();
            //int patientId = record.getPatientId();

            switch (recordType){

            // blood pressure checks
            case "SystolicPressure":{
                // systolic pressure check.
                if(systolicCriticalCheck(measurementValue)>=0){
                    // Systolic pressure low check
                    lowSystolic= systolicCriticalCheck(measurementValue) == 0;
                    triggerAlert(new Alert(""+record.getPatientId(), "CriticalSystolicPressure", record.getTimestamp()));
                }
                // if systolic pressure trend occurs.
                int increment = systolicTrend(prevSystolic,measurementValue);
                if (increment!=0 && prevSystolic !=0){
                    // if the trend is in the oppositie direction of previous trend.
                    if((systolicTrend>0&&increment==-1)||(systolicTrend<0&&increment==1)){
                        systolicTrend=0;
                    }
                    systolicTrend += increment;
                    // if positive or negative trend.
                    if(systolicTrend==3 || systolicTrend== -3){
                        systolicTrend=0;
                        triggerAlert(new Alert(""+record.getPatientId(), "SystolicPressureTrend", record.getTimestamp())); 
                    }
                }
                else {systolicTrend=0;}
                prevSystolic = measurementValue;
                break;
            }

            // diastolic pressure checks.
            case "DiastolicPressure":{
                // diastolic pressure Critical.
                if(diastolicCriticalCheck(measurementValue)>=0){
                    triggerAlert(new Alert(""+record.getPatientId(), "CriticalDiastolicPressure", record.getTimestamp())); 
                }
                // if systolic pressure trend occurs.
                int increment = diastolicTrend(prevDiastolic,measurementValue);
                if (increment!=0 && prevDiastolic !=0){
                    // if the trend is in the oppositie direction of previous trend.
                    if((diastolicTrend>0&&increment==-1)||(diastolicTrend<0&&increment==1)){
                        diastolicTrend=0;
                    }
                    diastolicTrend += increment;
                    // if positive or negative trend.
                    if(diastolicTrend==3||diastolicTrend== -3){
                        diastolicTrend=0;
                        triggerAlert(new Alert(""+record.getPatientId(), "DiastolicPressureTrend", record.getTimestamp())); 
                    }
                }
                else {diastolicTrend=0;}
                prevDiastolic = measurementValue;
                break;
            }
            // blood O2 saturation 
            case "Saturation":{
                // Low Saturation Alert
                if (measurementValue < 92) {
                    lowSat=true;
                    triggerAlert(new Alert(""+record.getPatientId(), "LowSaturation", record.getTimestamp()));
                } else {lowSat=false;}

                // Rapid Drop Alert
                double drop = prevOxygenSaturation - measurementValue;
                if (prevTimestamp != 0 && timestamp - prevTimestamp <= 600000 && drop >= 5) { // Within 10 minutes interval
                    triggerAlert(new Alert(""+record.getPatientId(), "rapidSaturationDrop", record.getTimestamp()));
                }
                prevOxygenSaturation = measurementValue;
                prevTimestamp = timestamp;
                break;
            }
            // ECG check.
            case "ECG":{

                // Calculate the average ECG value over the sliding window
                double ecgAverage = ecgSum / ecgWindow.size();

                // Check for significant peak
                if (Math.abs(measurementValue) > ecgAverage * PEAK_THRESHOLD_MULTIPLIER) {
                    triggerAlert(new Alert("" + record.getPatientId(), "SignificantEcgPeak", record.getTimestamp()));
                }

                // Add the current ECG value to the sliding window
                if (ecgWindow.size() == SLIDING_WINDOW_SIZE) {
                    ecgSum -= ecgWindow.poll();
                }
                
                ecgWindow.add(measurementValue);
                ecgSum += measurementValue;
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
    public List<Alert> getAllAlerts(){
        return this.alerts;
    }
    public Alert getAlertAt(int index){
        return this.alerts.get(index);
    }
    // Added methods.
    public int systolicTrend(double systolic1, double systolic2) {
        // Check for high trend
        if (systolic2>systolic1+10){return 1;}
        // Check for low trend
        else if (systolic2<systolic1-10){return -1;}
        return 0;
    }
    public int diastolicTrend(double diastolic1, double diastolic2) {
        // Check for high trend
        if (diastolic2>diastolic1 +10){return 1;}
        // Check for low trend
        else if (diastolic2<diastolic1-10){return -1;}
        return 0;
    }
    
    public int systolicCriticalCheck(double systolic) {
        // Check for Critical Threshold Alert
        if (systolic > 180) {return 1;}
        else if (systolic < 90) {return 0;}
        else return -1;
    }
    public int diastolicCriticalCheck(double diastolic) {
        // Check for Critical Threshold Alert
        if (diastolic > 120) {return 1;}
        else if (diastolic < 60) {return 0;}
        else return -1;
    }

    // Added methods.
}
