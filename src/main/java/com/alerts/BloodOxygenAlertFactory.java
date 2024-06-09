package com.alerts;

/**
 * The {@code BloodOxygenAlertFactory} class is a concrete implementation of the {@link AlertFactory}
 * abstract class. It is responsible for creating instances of {@link BloodOxygenAlert}.
 */
public class BloodOxygenAlertFactory extends AlertFactory {
    
    /**
     * Creates a new {@link BloodOxygenAlert} instance with the specified patient ID, condition, and timestamp.
     *
     * @param patientId  the ID of the patient for whom the alert is generated
     * @param condition  the condition that triggered the alert
     * @param timestamp  the time at which the alert was generated
     * @return a new instance of {@link BloodOxygenAlert}
     */
    @Override
    public Alert createAlert (String patientId, String condition, long timestamp){

        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}
