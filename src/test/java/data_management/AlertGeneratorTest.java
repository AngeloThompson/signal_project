package data_management;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.alerts.Alert;
import com.data_management.DataStorage;
import com.alerts.AlertGenerator;
import com.alerts.BloodPressureStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class AlertGeneratorTest {
    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;
    private Patient patient;

    @BeforeEach
    void setUp() {
        dataStorage = mock(DataStorage.class);
        alertGenerator = new AlertGenerator(dataStorage);
        patient = new Patient(1);
    }
    
    @Test
    @DisplayName("Low Systolic Pressure")
    void testSystolicPressureCriticalLow() {
        alertGenerator.setAlertStrategy(new BloodPressureStrategy());
        PatientRecord lowSystolic = new PatientRecord(1,85, "SystolicPressure", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(lowSystolic));

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolicPressure", alert.getCondition());
    }
    @Test
    @DisplayName("High Systolic Pressure")
    void testSystolicPressureCriticalHigh() {
        PatientRecord highSystolic = new PatientRecord(1,185, "SystolicPressure", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(highSystolic));

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolicPressure", alert.getCondition());
    }

    @Test
    @DisplayName("Systolic Pressure Increase Trend")
    void testSystolicPressureIncreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,100, "SystolicPressure", System.currentTimeMillis()-1000);
        PatientRecord record2 = new PatientRecord(1,111, "SystolicPressure", System.currentTimeMillis()-500);
        PatientRecord record3 = new PatientRecord(1,122, "SystolicPressure", System.currentTimeMillis()-100);
        PatientRecord record4 = new PatientRecord(1,133, "SystolicPressure", System.currentTimeMillis());

        List<PatientRecord> records = Arrays.asList(record1, record2, record3,record4);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);
        alertGenerator = new AlertGenerator(dataStorage);
        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("SystolicPressureTrend", alert.getCondition());
    }
    @Test
    @DisplayName("Systolic Pressure Decrease Trend")
    void testSystolicPressureDecreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,150, "SystolicPressure", System.currentTimeMillis()-10000);
        PatientRecord record2 = new PatientRecord(1,130, "SystolicPressure", System.currentTimeMillis()-5000);
        PatientRecord record3 = new PatientRecord(1,110, "SystolicPressure", System.currentTimeMillis()-100);
        PatientRecord record4 = new PatientRecord(1,99, "SystolicPressure", System.currentTimeMillis());
        
        List<PatientRecord> records = Arrays.asList(record1, record2, record3,record4);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("SystolicPressureTrend", alert.getCondition());
    }

    @Test
    @DisplayName("High Diastolic Pressure")
    void testDiastolicPressureCriticalHigh() {
        PatientRecord highDiastolic = new PatientRecord(1,130, "DiastolicPressure", System.currentTimeMillis());

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(highDiastolic));

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalDiastolicPressure", alert.getCondition());
    }
    @Test
    @DisplayName("Low Diastolic Pressure")
    void testDiastolicPressureCriticalLow() {
        PatientRecord highDiastolic = new PatientRecord(1,55, "DiastolicPressure", System.currentTimeMillis());

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(highDiastolic));

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalDiastolicPressure", alert.getCondition());
    }
    @Test
    @DisplayName("Diastolic Pressure Increase Trend")
    void testDiastolicPressureIncreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,80, "DiastolicPressure", System.currentTimeMillis()-10000);
        PatientRecord record2 = new PatientRecord(1,91, "DiastolicPressure", System.currentTimeMillis()-5000);
        PatientRecord record3 = new PatientRecord(1,102, "DiastolicPressure", System.currentTimeMillis()-100);
        PatientRecord record4 = new PatientRecord(1,113, "DiastolicPressure", System.currentTimeMillis());
        
        List<PatientRecord> records = Arrays.asList(record1, record2, record3,record4);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        //ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        //Alert alert = alertCaptor.getValue();
        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("DiastolicPressureTrend", alert.getCondition());
    }
    @Test
    @DisplayName("Diastolic Pressure Decrease Trend")
    void testDiastolicPressureDecreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,110, "DiastolicPressure", System.currentTimeMillis()-10000);
        PatientRecord record2 = new PatientRecord(1,99, "DiastolicPressure", System.currentTimeMillis()-5000);
        PatientRecord record3 = new PatientRecord(1,88, "DiastolicPressure", System.currentTimeMillis()-100);
        PatientRecord record4 = new PatientRecord(1,77, "DiastolicPressure", System.currentTimeMillis());
        
        List<PatientRecord> records = Arrays.asList(record1, record2, record3, record4);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("DiastolicPressureTrend", alert.getCondition());
    }

    @Test
    @DisplayName("Low Oxygen Saturation")
    void testOxygenSaturationLow() {
        PatientRecord lowSaturation = new PatientRecord(1,88, "Saturation", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(lowSaturation));

        alertGenerator.evaluateData(patient);

        // ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        // verify(dataStorage, times(1)).triggerAlert(alertCaptor.capture());
        //Alert alert = alertCaptor.getValue();
        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("LowSaturation", alert.getCondition());
    }
    @Test
    @DisplayName("Rapid Saturation Drop")
    void testOxygenSaturationDrop() {
        PatientRecord saturation1 = new PatientRecord(1,95, "Saturation", System.currentTimeMillis());
        PatientRecord saturation2 = new PatientRecord(1,110, "Saturation", System.currentTimeMillis()-100);

        List<PatientRecord> records = Arrays.asList(saturation2, saturation1);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("rapidSaturationDrop", alert.getCondition());
    }
    @Test
    @DisplayName("Hypotensive Hypoxemia")
    void testHypotensiveHypoxia() {
        PatientRecord record1 = new PatientRecord(1,91, "Saturation", System.currentTimeMillis()-100);
        PatientRecord record2 = new PatientRecord(1,89, "SystolicPressure", System.currentTimeMillis());

        List<PatientRecord> records = Arrays.asList(record1, record2);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        Alert alert1 = alertGenerator.getAlertAt(0);
        Alert alert2 = alertGenerator.getAlertAt(1);
        Alert alert3 = alertGenerator.getAlertAt(2);

        assertEquals("1", alert1.getPatientId());
        assertEquals("LowSaturation", alert1.getCondition());
        assertEquals("CriticalSystolicPressure", alert2.getCondition());
        assertEquals("HypotensiveHypoxemia", alert3.getCondition());
    }
    @Test
    @DisplayName("ECG peak")
    void testSignificantEcgPeakDetection() {
        PatientRecord record1 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-1000);
        PatientRecord record2 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-900);
        PatientRecord record3 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-800);
        PatientRecord record4 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-700);
        PatientRecord record5 = new PatientRecord(1,0.85, "ECG", System.currentTimeMillis());

        List<PatientRecord> records = Arrays.asList(record1, record2,record3,record4,record5);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("SignificantEcgPeak", alert.getCondition());
    }
    @Test
    @DisplayName("ECG negative peak")
    void testSignificantEcgPeakNegative() {
        PatientRecord record1 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-1000);
        PatientRecord record2 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-900);
        PatientRecord record3 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-800);
        PatientRecord record4 = new PatientRecord(1,0.5, "ECG", System.currentTimeMillis()-700);
        PatientRecord record5 = new PatientRecord(1,-0.85, "ECG", System.currentTimeMillis());

        List<PatientRecord> records = Arrays.asList(record1, record2,record3,record4,record5);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);

        assertEquals("1", alert.getPatientId());
        assertEquals("SignificantEcgPeak", alert.getCondition());
    }
    @Test
    @DisplayName("Sliding window test")
    public void testEcgSlidingWindowAverage() {
        // Setup: Create a patient and some ECG records
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            records.add(new PatientRecord(1, 1.0 + i * 0.1,"ECG", System.currentTimeMillis() - (10000 - i * 1000)));
        }
        records.add(new PatientRecord(1, 5.0,"ECG", System.currentTimeMillis() - 500)); // peak

        // Return records.
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        // Execute: Evaluate the patient data
        alertGenerator.evaluateData(patient);

        // Verify: Check if the alert for significant ECG peak is triggered
        List<Alert> alerts = alertGenerator.getAllAlerts();
        assertFalse(alerts.isEmpty());
        assertEquals("SignificantEcgPeak", alerts.get(0).getCondition());
    }
}
