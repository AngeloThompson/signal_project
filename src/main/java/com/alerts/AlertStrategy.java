package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

public interface AlertStrategy {

    public abstract Alert checkAlert(Patient patient, List<PatientRecord> records);
}
