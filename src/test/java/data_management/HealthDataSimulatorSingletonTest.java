package data_management;

import org.junit.jupiter.api.Test;

import com.cardio_generator.HealthDataSimulator;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class HealthDataSimulatorSingletonTest {

    @BeforeEach
    void resetSingleton() {
        // Reset the singleton instance to null before each test to ensure isolation between tests
        HealthDataSimulator.setInstance(null);
    }

    @Test
    void testSingletonInstance() {
        // Get the first instance
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        // Get the second instance
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        
        // Verify that both instances are the same
        assertSame(instance1, instance2, "Both instances should be the same");
    }

    @Test
    void testSingletonIsNotNull() {
        // Get the instance
        HealthDataSimulator instance = HealthDataSimulator.getInstance();
        
        // Verify that the instance is not null
        assertNotNull(instance, "Instance should not be null");
    }
}

