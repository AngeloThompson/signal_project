package data_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.Patient;

import java.util.List;

class DataStorageTest {

    @Test
    @DisplayName("Add and get records")
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        //assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }
    @Test
    @DisplayName("get All Patients")
    void testGetAllPatients(){
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(2, 200.0, "WhiteBloodCells", 1714376789051L);
        storage.addPatientData(3, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(4, 200.0, "WhiteBloodCells", 1714376789051L);

        List<Patient> allPatients = storage.getAllPatients();
        assertEquals(4,allPatients.size());
    }
}
