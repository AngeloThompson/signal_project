package data_management;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.DisplayName;

import com.alerts.Alert;
import com.data_management.DataStorage;
import com.alerts.AlertGenerator;
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
        Patient patient = new Patient(1);
    }
    
    @Test
    @DisplayName("Low Systolic Pressure")
    void testSystolicPressureCriticalLow() {
        PatientRecord lowSystolic = new PatientRecord(1,185, "SystolicPressure", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(lowSystolic));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolicPressure", alert.getCondition());
    }
    @Test
    @DisplayName("High Systolic Pressure")
    void testSystolicPressureCriticalHigh() {
        PatientRecord highSystolic = new PatientRecord(1,85, "SystolicPressure", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(highSystolic));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolicPressure", alert.getCondition());
    }

    @Test
    @DisplayName("Systolic Pressure Increase Trend")
    void testSystolicPressureIncreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,100, "SystolicPressure", System.currentTimeMillis()-10000);
        PatientRecord record2 = new PatientRecord(1,111, "SystolicPressure", System.currentTimeMillis()-5000);
        PatientRecord record3 = new PatientRecord(1,122, "SystolicPressure", System.currentTimeMillis());
        
        List<PatientRecord> records = Arrays.asList(record1, record2, record3);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("SystolicPressureTrend", alert.getCondition());
    }
    @DisplayName("Systolic Pressure Decrease Trend")
    void testSystolicPressureDecreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,100, "SystolicPressure", System.currentTimeMillis()-10000);
        PatientRecord record2 = new PatientRecord(1,99, "SystolicPressure", System.currentTimeMillis()-5000);
        PatientRecord record3 = new PatientRecord(1,88, "SystolicPressure", System.currentTimeMillis());
        
        List<PatientRecord> records = Arrays.asList(record1, record2, record3);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("SystolicPressureTrend", alert.getCondition());
    }

    @Test
    @DisplayName("High Diastolic Pressure")
    void testDiastolicPressureCriticalHigh() {
        PatientRecord highDiastolic = new PatientRecord(1,130, "DiastolicPressure", System.currentTimeMillis());

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(highDiastolic));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalDiastolicPressure", alert.getCondition());
    }
    @Test
    @DisplayName("Low Diastolic Pressure")
    void testDiastolicPressureCriticalLow() {
        PatientRecord highDiastolic = new PatientRecord(1,55, "DiastolicPressure", System.currentTimeMillis());

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(highDiastolic));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalDiastolicPressure", alert.getCondition());
    }
    @Test
    @DisplayName("Diastolic Pressure Increase Trend")
    void testDiastolicPressureIncreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,100, "DiastolicPressure", System.currentTimeMillis()-10000);
        PatientRecord record2 = new PatientRecord(1,111, "DiastolicPressure", System.currentTimeMillis()-5000);
        PatientRecord record3 = new PatientRecord(1,122, "DiastolicPressure", System.currentTimeMillis());
        
        List<PatientRecord> records = Arrays.asList(record1, record2, record3);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("DiastolicPressureTrend", alert.getCondition());
    }
    @Test
    @DisplayName("Diastolic Pressure Decrease Trend")
    void testDiastolicPressureDecreaseTrend() {
        PatientRecord record1 = new PatientRecord(1,100, "DiastolicPressure", System.currentTimeMillis()-10000);
        PatientRecord record2 = new PatientRecord(1,99, "DiastolicPressure", System.currentTimeMillis()-5000);
        PatientRecord record3 = new PatientRecord(1,88, "DiastolicPressure", System.currentTimeMillis());
        
        List<PatientRecord> records = Arrays.asList(record1, record2, record3);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("DiastolicPressureTrend", alert.getCondition());
    }

    @Test
    @DisplayName("Low Oxygen Saturation")
    void testOxygenSaturationLow() {
        PatientRecord lowSaturation = new PatientRecord(1,88, "Saturation", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(lowSaturation));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        // verify(dataStorage, times(1)).triggerAlert(alertCaptor.capture());
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("LowSaturation", alert.getCondition());
    }
    @Test
    @DisplayName("Rapid Saturation Drop")
    void testOxygenSaturationDrop() {
        PatientRecord saturation1 = new PatientRecord(1,95, "Saturation", System.currentTimeMillis());
        PatientRecord saturation2 = new PatientRecord(1,100, "Saturation", System.currentTimeMillis()-100);

        List<PatientRecord> records = Arrays.asList(saturation1, saturation2);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("rapidSaturationDrop", alert.getCondition());
    }
    @Test
    @DisplayName("Hypotensive Hypoxemia")
    void testHypotensiveHypoxia() {
        PatientRecord record1 = new PatientRecord(1,89, "SystolicPressure", System.currentTimeMillis());
        PatientRecord record2 = new PatientRecord(1,91, "Saturation", System.currentTimeMillis()-100);

        List<PatientRecord> records = Arrays.asList(record1, record2);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getAllAlerts();
        Alert alert1 = alerts.get(0);
        Alert alert2 = alerts.get(1);
        Alert alert3 = alerts.get(2);

        assertEquals("1", alert1.getPatientId());
        assertEquals("CriticalSystolicPressure", alert1.getCondition());
        assertEquals("LowSaturation", alert2.getCondition());
        assertEquals("HypotensiveHypoxemia", alert3.getCondition());
    }
    @Test
    @DisplayName("Low Bpm")
    void testBPMLow() {
        PatientRecord BPM = new PatientRecord(1,59, "ECG", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(BPM));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalBPM", alert.getCondition());
    }
    @Test
    @DisplayName("High Bpm")
    void testBPMHigh() {
        PatientRecord BPM = new PatientRecord(1,181, "ECG", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(BPM));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalHeartRate", alert.getCondition());
    }

    @Test
    @DisplayName("Irregular ECG checks")
    void testECGAbnormalHeartRate() {
        PatientRecord record1 = new PatientRecord(1,100, "ECG", System.currentTimeMillis()-1000);
        PatientRecord record2 = new PatientRecord(1,100, "ECG", System.currentTimeMillis()-900);
        PatientRecord record3 = new PatientRecord(1,100, "ECG", System.currentTimeMillis()-800);
        PatientRecord record4 = new PatientRecord(1,100, "ECG", System.currentTimeMillis()-700);
        PatientRecord record5 = new PatientRecord(1,150, "ECG", System.currentTimeMillis());

        List<PatientRecord> records = Arrays.asList(record1, record2,record3,record4,record5);

        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Alert alert = alertCaptor.getValue();

        assertEquals("1", alert.getPatientId());
        assertEquals("IrregularHeartRate", alert.getCondition());
    }
}
