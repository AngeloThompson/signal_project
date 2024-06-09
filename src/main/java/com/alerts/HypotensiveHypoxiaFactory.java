package com.alerts;

/**
 * The {@code HypotensiveHypoxiaFactory} class is a concrete implementation of the {@link AlertFactory}
 * abstract class. It is responsible for creating instances of {@link HypotensiveHypoxiaAlert}.
 */
public class HypotensiveHypoxiaFactory extends AlertFactory{

    /**
     * Creates a new {@link HypotensiveHypoxiaAlert} instance with the specified patient ID, condition, and timestamp.
     *
     * @param patientId  the ID of the patient for whom the alert is generated.
     * @param condition  the condition that triggered the alert.
     * @param timestamp  the time at which the alert was generated.
     * @return a new instance of {@link HypotensiveHypoxiaAlert}
     */
    @Override
    public Alert createAlert (String patientId, String condition, long timestamp){

        return new HypotensiveHypoxiaAlert(patientId, condition, timestamp);
    }
}
