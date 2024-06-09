package data_management;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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
import com.alerts.OxygenSaturationStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class StrategyPatternTest {
    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;
    private Patient patient;

    @BeforeEach
    void setUp() {
        dataStorage = mock(DataStorage.class);
        DataStorage.setInstance(dataStorage); // Set the mock instance
        alertGenerator = new AlertGenerator(dataStorage);
        patient = new Patient(1);
    }
    @Test
    @DisplayName("Change Strategy")
    void testChangeStrategyDrop() {
        // set Strategy
        alertGenerator.setAlertStrategy(new OxygenSaturationStrategy());

        PatientRecord saturation1 = new PatientRecord(1,95, "Saturation", System.currentTimeMillis());
        PatientRecord saturation2 = new PatientRecord(1,110, "Saturation", System.currentTimeMillis()-100);

        List<PatientRecord> records = Arrays.asList(saturation2, saturation1);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        // set second Strategy
        alertGenerator.setAlertStrategy(new BloodPressureStrategy());
        PatientRecord lowSystolic = new PatientRecord(1,85, "SystolicPressure", System.currentTimeMillis());
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(lowSystolic));

        alertGenerator.evaluateData(patient);

        Alert alert = alertGenerator.getAlertAt(0);
        Alert alert2 = alertGenerator.getAlertAt(1);

        assertEquals("1", alert.getPatientId());
        assertEquals("rapidSaturationDrop", alert.getCondition());
        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolicPressure", alert2.getCondition());
    }
}