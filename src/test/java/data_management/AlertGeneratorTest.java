package data_management;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.alerts.Alert;
import com.data_management.DataStorage;
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
        patient = new Patient("12345"); // Assuming a constructor with patient ID
    }
    
    @Test
    void testSystolicPressureCriticalLow() {
        PatientRecord lowSystolic = new PatientRecord("SystolicPressure", 85, System.currentTimeMillis(), "12345");
        when(dataStorage.getRecords(anyString(), anyLong(), anyLong())).thenReturn(Arrays.asList(lowSystolic));

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(dataStorage, times(1)).triggerAlert(alertCaptor.capture());
        Alert alert = alertCaptor.getValue();

        assertEquals("12345", alert.getPatientId());
        assertEquals("CriticalSystolicPressure", alert.getAlertType());
    }

    @Test
    void testSystolicPressureTrend() {
        PatientRecord record1 = new PatientRecord("SystolicPressure", 100, System.currentTimeMillis() - 10000, "12345");
        PatientRecord record2 = new PatientRecord("SystolicPressure", 110, System.currentTimeMillis() - 5000, "12345");
        PatientRecord record3 = new PatientRecord("SystolicPressure", 120, System.currentTimeMillis(), "12345");
        List<PatientRecord> records = Arrays.asList(record1, record2, record3);

        when(dataStorage.getRecords(anyString(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(dataStorage, times(1)).triggerAlert(alertCaptor.capture());
        Alert alert = alertCaptor.getValue();

        assertEquals("12345", alert.getPatientId());
        assertEquals("SystolicPressureTrend", alert.getAlertType());
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
