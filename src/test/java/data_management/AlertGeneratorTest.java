package data_management;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        assertEquals("LowSystolicPressure", alert.getCondition());
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
        assertEquals("highSystolicPressure", alert.getCondition());
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
        assertEquals("SystolicPressureIncreaseTrend", alert.getCondition());
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
        assertEquals("SystolicPressureDecreaseTrend", alert.getCondition());
    }

    @Test
    void testDiastolicPressureCriticalHigh() {
        PatientRecord highDiastolic = new PatientRecord("DiastolicPressure", 130, System.currentTimeMillis(), "12345");
        when(dataStorage.getRecords(anyString(), anyLong(), anyLong())).thenReturn(Arrays.asList(highDiastolic));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(dataStorage, times(1)).triggerAlert(alertCaptor.capture());
        Alert alert = alertCaptor.getValue();

        assertEquals("12345", alert.getPatientId());
        assertEquals("CriticalDiastolicPressure", alert.getAlertType());
    }

    @Test
    void testOxygenSaturationLow() {
        PatientRecord lowSaturation = new PatientRecord("Saturation", 88, System.currentTimeMillis(), "12345");
        when(dataStorage.getRecords(anyString(), anyLong(), anyLong())).thenReturn(Arrays.asList(lowSaturation));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(dataStorage, times(1)).triggerAlert(alertCaptor.capture());
        Alert alert = alertCaptor.getValue();

        assertEquals("12345", alert.getPatientId());
        assertEquals("LowSaturation", alert.getAlertType());
    }

    @Test
    void testECGAbnormalHeartRate() {
        PatientRecord abnormalECG = new PatientRecord("ECG", 120, System.currentTimeMillis(), "12345");
        when(dataStorage.getRecords(anyString(), anyLong(), anyLong())).thenReturn(Arrays.asList(abnormalECG));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(dataStorage, times(1)).triggerAlert(alertCaptor.capture());
        Alert alert = alertCaptor.getValue();

        assertEquals("12345", alert.getPatientId());
        assertEquals("AbnormalHeartRate", alert.getAlertType());
    }
}
