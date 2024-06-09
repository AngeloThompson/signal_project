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
    private AlertStrategy alertStrategy;
    // private static final int SLIDING_WINDOW_SIZE = 10; // Size of the sliding window for averaging ECG values
    // private static final double PEAK_THRESHOLD_MULTIPLIER = 1.5; // Multiplier to determine significant peaks

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

    public void setAlertStrategy(AlertStrategy alertStrategy) {
        this.alertStrategy = alertStrategy;
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
        if (alertStrategy != null) {
            List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), System.currentTimeMillis() - 1200000, System.currentTimeMillis());
            Alert triggeredAlert = alertStrategy.checkAlert(patient, records);
            if (triggeredAlert != null){
                triggerAlert(triggeredAlert);
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
