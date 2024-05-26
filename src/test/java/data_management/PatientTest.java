package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class PatientTest {
    @Test
    @DisplayName("get All Patients")
    void testGetAllPatients(){
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "WhiteBloodCells", 1714376789050L);
        patient.addRecord(200.0, "WhiteBloodCells", 1714376789051L);
        patient.addRecord(100.0, "WhiteBloodCells", 1714376789050L);
        patient.addRecord(200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> allRecords = patient.getRecords(1714376789050L,1714376789051L);
        assertEquals(4,allRecords.size());
    }
}
