package com.alerts;

public class HypotensiveHypoxiaFactory extends AlertFactory{
    @Override
    public Alert createAlert (String patientId, String condition, long timestamp){

        return new HypotensiveHypoxiaAlert(patientId, condition, timestamp);
    }
}
