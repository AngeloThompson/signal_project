package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {

    private AlertFactory factory;

    @Override
    public Alert checkAlert(Patient patient, List<PatientRecord> records) {
        int systolicTrend = 0;
        double prevSystolic = 0;
        int diastolicTrend = 0;
        double prevDiastolic = 0;
        factory = new BloodPressureAlertFactory();

        for (PatientRecord record : records) {
            String recordType = record.getRecordType();
            double measurementValue = record.getMeasurementValue();

            switch (recordType) {
                case "SystolicPressure":

                    if (systolicCriticalCheck(measurementValue) >= 0) {
                        return factory.createAlert("" + record.getPatientId(), "CriticalSystolicPressure", record.getTimestamp());
                    }

                    int increment = systolicTrend(prevSystolic, measurementValue);
                    if (increment != 0 && prevSystolic != 0) {
                        if ((systolicTrend > 0 && increment == -1) || (systolicTrend < 0 && increment == 1)) {
                            systolicTrend = 0;
                        }

                        systolicTrend += increment;
                        if (systolicTrend == 3 || systolicTrend == -3) {
                            systolicTrend = 0;
                            return factory.createAlert("" + record.getPatientId(), "SystolicPressureTrend", record.getTimestamp());
                        }

                    } else {systolicTrend = 0;}
                    prevSystolic = measurementValue;
                    break;

                case "DiastolicPressure":
                    if (diastolicCriticalCheck(measurementValue) >= 0) {
                        return factory.createAlert("" + record.getPatientId(), "CriticalDiastolicPressure", record.getTimestamp());
                    }
                    increment = diastolicTrend(prevDiastolic, measurementValue);
                    if (increment != 0 && prevDiastolic != 0) {
                        if ((diastolicTrend > 0 && increment == -1) || (diastolicTrend < 0 && increment == 1)) {
                            diastolicTrend = 0;
                        }
                        diastolicTrend += increment;
                        if (diastolicTrend == 3 || diastolicTrend == -3) {
                            diastolicTrend = 0;
                            return factory.createAlert("" + record.getPatientId(), "DiastolicPressureTrend", record.getTimestamp());
                        }
                    } else {
                        diastolicTrend = 0;
                    }
                    prevDiastolic = measurementValue;
                    break;
            }
        } 
        return null;
    }

    private int systolicTrend(double systolic1, double systolic2) {
        if (systolic2 > systolic1 + 10) { return 1; }
        else if (systolic2 < systolic1 - 10) { return -1; }
        return 0;
    }

    private int diastolicTrend(double diastolic1, double diastolic2) {
        if (diastolic2 > diastolic1 + 10) { return 1; }
        else if (diastolic2 < diastolic1 - 10) { return -1; }
        return 0;
    }

    private int systolicCriticalCheck(double systolic) {
        if (systolic > 180) { return 1; }
        else if (systolic < 90) { return 0; }
        else { return -1; }
    }

    private int diastolicCriticalCheck(double diastolic) {
        if (diastolic > 120) { return 1; }
        else if (diastolic < 60) { return 0; }
        else { return -1; }
    }
}
