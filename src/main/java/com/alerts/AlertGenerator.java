package com.alerts;

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
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(),System.currentTimeMillis(),(long)(System.currentTimeMillis()+1200000));
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
            // blood pressure checks
            if (record.getRecordType().equals("SystolicPressure")){
                // systolic pressure check.
                if(systolicCriticalCheck(record.getMeasurementValue())>=0){
                    // Systolic pressure low
                    if(systolicCriticalCheck(record.getMeasurementValue())==0){
                        lowSystolic=true;
                    }
                    else{lowSystolic=false;}
                    Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                    triggerAlert(alert); 
                }
                // if systolic pressure trend occurs.
                int increment = systolicTrend(prevSystolic,record.getMeasurementValue());
                if (increment!=0){
                    // if the trend is in the oppositie direction of previous trend.
                    if((systolicTrend>0&&increment==-1)||(systolicTrend<0&&increment==1)){systolicTrend=0;}
                    
                    systolicTrend += increment;
                    // if positive trend.
                    if(systolicTrend==3){
                        systolicTrend=0;
                        Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                        triggerAlert(alert); 
                    }
                    // if negetaive trend.
                    else if(systolicTrend== -3){
                        systolicTrend=0;
                        Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                        triggerAlert(alert); 
                    }
                }
                else {systolicTrend=0;}
                prevSystolic = record.getMeasurementValue();
            }
            // diastolic pressure checks.
            else if (record.getRecordType().equals("DiastolicPressure")){
                // diastolic pressure high.
                if(diastolicCriticalCheck(record.getMeasurementValue())>=0){
                    Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                    triggerAlert(alert); 
                }
                // if systolic pressure trend occurs.
                int increment = diastolicTrend(prevSystolic,record.getMeasurementValue());
                if (increment!=0){
                    // if the trend is in the oppositie direction of previous trend.
                    if((diastolicTrend>0&&increment==-1)||(diastolicTrend<0&&increment==1)){diastolicTrend=0;}
                    diastolicTrend += increment;
                    // if positive trend.
                    if(diastolicTrend==3){
                        systolicTrend=0;
                        Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                        triggerAlert(alert); 
                    }
                    // if negetaive trend.
                    else if(diastolicTrend== -3){
                        systolicTrend=0;
                        Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                        triggerAlert(alert); 
                    }
                }
                else {systolicTrend=0;}
                prevDiastolic = record.getMeasurementValue();
            }
            // blood O2 saturation 
            else if (record.getRecordType().equals("Saturation")){
                    // Low Saturation Alert
                    if (record.getMeasurementValue() < 92) {
                        lowSat=true;
                        triggerAlert(new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp()));
                    }
                    else{lowSat=false;}
                    // Rapid Drop Alert
                    if (prevTimestamp != 0 && record.getTimestamp() - prevTimestamp <= 600000) { // Within 10 minutes interval
                        double drop = prevOxygenSaturation - record.getMeasurementValue();
                        if (drop >= 5) {
                            triggerAlert(new Alert(""+record.getPatientId(), "Rapid Drop Alert", record.getTimestamp()));
                        }
                    }
                    prevOxygenSaturation = record.getMeasurementValue();
                    prevTimestamp = record.getTimestamp();
            }
            // ECG check.
            else if (record.getRecordType().equals("ECG")){
                // Abnormal heart rate check.
                if(record.getMeasurementValue() <50 ||record.getMeasurementValue()>100){
                    Alert alert = new Alert(""+record.getPatientId(), record.getRecordType(), record.getTimestamp());
                    triggerAlert(alert); 
                }
                // Irregular beat pattern check.
                final int abnormalThreshold=30; // Threshold for abnormal ECG readings.
                if (record.getMeasurementValue()>=prevBPM+abnormalThreshold||record.getMeasurementValue()<prevBPM-abnormalThreshold) { 
                    triggerAlert(new Alert(""+record.getPatientId(), "Irregular_BPM", record.getTimestamp()));
                }
                prevBPM = record.getMeasurementValue();
            }
            // Hypotensive hypoxia check 
            if (lowSystolic&&lowSat){
                Alert alert = new Alert(""+record.getPatientId(), "HypotensiveHypoxemia", record.getTimestamp());
                triggerAlert(alert);
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
        alerts.add(alert);
    }
    // Added methods.
    public int systolicTrend(double systolic1, double systolic2) {
        // Check for high trend
        double systolicTemp=systolic1+10;
        if (systolic2>=systolicTemp){return 1;}
        // Check for low trend
        systolicTemp=systolic1-10;
        if (systolic2<=systolicTemp){return -1;}
        return 0;
    }
    public int diastolicTrend(double diastolic1, double diastolic2) {
        // Check for high trend
        double systolicTemp=diastolic1+10;
        if (diastolic2>=systolicTemp){return 1;}
        // Check for low trend
        systolicTemp=diastolic1-10;
        if (diastolic2<=systolicTemp){return -1;}
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
