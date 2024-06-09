package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;
import com.alerts.BloodPressureAlert;
import com.alerts.AlertFactory;
import com.alerts.BloodOxygenAlertFactory;
import com.alerts.BloodPressureAlertFactory;
import com.alerts.ECGAlert;
import com.alerts.ECGAlertFactory;
import com.alerts.HypotensiveHypoxiaAlert;
import com.alerts.HypotensiveHypoxiaFactory;

public class FactoryPatternTest {
    private AlertFactory factory;
    
    @Test
    @DisplayName("test BloodOxygenAlertFactory")
    void testFactoryAlerts() {
        
        factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert(null, null, 0);
        assertTrue(alert instanceof BloodOxygenAlert, "Alert should be an instance of BloodOxygenAlert");
    }
    @Test
    @DisplayName("test BloodPressureAlertFactory")
    void testBloodPressureAlerts() {
        
        factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert(null, null, 0);
        assertTrue(alert instanceof BloodPressureAlert, "Alert should be an instance of BloodPressureAlert");
    }
    @Test
    @DisplayName("test ECGAlertFactory")
    void testECGAlerts() {
        
        factory = new ECGAlertFactory();
        Alert alert = factory.createAlert(null, null, 0);
        assertTrue(alert instanceof ECGAlert, "Alert should be an instance of ECGAlert");
    }
    @Test
    @DisplayName("test HypotensiveHypoxiaFactory")
    void testHypotensiveHypoxiaAlerts() {
        
        factory = new HypotensiveHypoxiaFactory();
        Alert alert = factory.createAlert(null, null, 0);
        assertTrue(alert instanceof HypotensiveHypoxiaAlert, "Alert should be an instance of HypotensiveHypoxiaAlert");
    }
}