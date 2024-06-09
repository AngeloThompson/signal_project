package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * The {@code BloodPressureStrategy} class implements the {@link AlertStrategy} interface.
 * This strategy is responsible for monitoring patient records for blood pressure trends
 * and critical values, and generating alerts when specific conditions are met.
 */
public class BloodPressureStrategy implements AlertStrategy {

    private AlertFactory factory;

    /**
     * Checks the patient's records for blood pressure alerts.
     *
     * This method iterates through the provided patient records to detect
     * critical systolic and diastolic blood pressure values, as well as trends
     * in blood pressure changes. If any such condition is met, an appropriate alert
     * is generated using the {@link BloodPressureAlertFactory}.
     *
     * @param patient the patient whose records are being evaluated
     * @param records the list of patient records to evaluate
     * @return an {@link Alert} if any alert condition is met, otherwise {@code null}
     */
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

    /**
     * Determines the trend in systolic blood pressure.
     *
     * @param systolic1 the previous systolic blood pressure value
     * @param systolic2 the current systolic blood pressure value
     * @return 1 if there is an increasing trend, -1 if there is a decreasing trend, 0 otherwise
     */
    private int systolicTrend(double systolic1, double systolic2) {
        if (systolic2 > systolic1 + 10) { return 1; }
        else if (systolic2 < systolic1 - 10) { return -1; }
        return 0;
    }

    /**
     * Determines the trend in diastolic blood pressure.
     *
     * @param diastolic1 the previous diastolic blood pressure value
     * @param diastolic2 the current diastolic blood pressure value
     * @return 1 if there is an increasing trend, -1 if there is a decreasing trend, 0 otherwise
     */
    private int diastolicTrend(double diastolic1, double diastolic2) {
        if (diastolic2 > diastolic1 + 10) { return 1; }
        else if (diastolic2 < diastolic1 - 10) { return -1; }
        return 0;
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

    /**
     * Checks the diastolic blood pressure for critical thresholds.
     *
     * @param diastolic the diastolic blood pressure value to check
     * @return 1 if the diastolic pressure is critically high, 0 if it is critically low, -1 otherwise
     */
    private int diastolicCriticalCheck(double diastolic) {
        if (diastolic > 120) { return 1; }
        else if (diastolic < 60) { return 0; }
        else { return -1; }
    }
}
