package data_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.Patient;

import java.util.List;

class DataStorageTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        storage = new DataStorage();
    }

    @Test
    @DisplayName("Add and get records")
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        //assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }
    @Test
    @DisplayName("get All Patients")
    void testGetAllPatients(){
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(2, 200.0, "WhiteBloodCells", 1714376789051L);
        storage.addPatientData(3, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(4, 200.0, "WhiteBloodCells", 1714376789051L);

        List<Patient> allPatients = storage.getAllPatients();
        assertEquals(4,allPatients.size());
    }
    @Test
    void testAddAndRetrievePatientData() {
        storage.addPatientData(1, 72.5, "HeartRate", 1627849261000L);
        List<PatientRecord> records = storage.getRecords(1, 1627849260000L, 1627849262000L);
        assertEquals(1, records.size());
        assertEquals("HeartRate", records.get(0).getRecordType());
    }

    @Test
    void testGetRecordsForNonexistentPatient() {
        List<PatientRecord> records = storage.getRecords(999, 1627849260000L, 1627849262000L);
        assertTrue(records.isEmpty());
    }

    @Test
    void testConcurrentDataUpdates() throws InterruptedException {
        Thread thread1 = new Thread(() -> storage.addPatientData(1, 72.5, "HeartRate", 1627849261000L));
        Thread thread2 = new Thread(() -> storage.addPatientData(1, 120.8, "BloodPressure", 1627849262000L));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        List<PatientRecord> records = storage.getRecords(1, 1627849260000L, 1627849263000L);
        assertEquals(2, records.size());
    }
}
