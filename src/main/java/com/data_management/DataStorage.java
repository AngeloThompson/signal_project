package com.data_management;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock; 

import com.alerts.AlertGenerator;


/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {

    private static volatile DataStorage instance;
    private ConcurrentHashMap<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(); // ensures that reading and writing operations do not conflict.
    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage() {
        this.patientMap = new ConcurrentHashMap<>(); // thread-safe access and updates to the patient map.
    }
    /**
     * Provides the global point of access to the DataStorage instance.
     *
     * @return the singleton instance of DataStorage
     */
    public static synchronized DataStorage getInstance() {
        
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }
    // set the singleton instance for testing
    public static synchronized void setInstance(DataStorage mockInstance) {
        instance = mockInstance;
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        lock.writeLock().lock();
        try {
            Patient patient = patientMap.get(patientId);
            if (patient == null) {
                patient = new Patient(patientId);
                patientMap.put(patientId, patient);
            }
            patient.addRecord(measurementValue, recordType, timestamp);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        lock.readLock().lock(); // ensures that reading and writing operations do not conflict.
        try {
            Patient patient = patientMap.get(patientId);
            if (patient != null) {
                return patient.getRecords(startTime, endTime);
            }
            return new ArrayList<>(); // return an empty list if no patient is found
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(patientMap.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // DataReader is not defined in this scope, should be initialized appropriately.
        // DataReader reader = new SomeDataReaderImplementation("path/to/data");
        DataStorage storage = new DataStorage();

        // Assuming the reader has been properly initialized and can read data into the
        // storage
        // reader.readData(storage);

        // Example of using DataStorage to retrieve and print records for a patient
        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId() +
                    ", Type: " + record.getRecordType() +
                    ", Data: " + record.getMeasurementValue() +
                    ", Timestamp: " + record.getTimestamp());
        }

        // Initialize the AlertGenerator with the storage
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        // Evaluate all patients' data to check for conditions that may trigger alerts
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }
}
