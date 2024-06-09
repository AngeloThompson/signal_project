package data_management;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class DataStorageSingletonTest {

    @BeforeEach
    void resetSingleton() {
        // Reset the singleton instance to null before each test to ensure isolation between tests
        DataStorage.setInstance(null);
    }

    @Test
    void testSingletonInstance() {
        // Get the first instance
        DataStorage instance1 = DataStorage.getInstance();
        // Get the second instance
        DataStorage instance2 = DataStorage.getInstance();
        
        // Verify that both instances are the same
        assertSame(instance1, instance2, "Both instances should be the same");
    }

    @Test
    void testSingletonIsNotNull() {
        // Get the instance
        DataStorage instance = DataStorage.getInstance();
        
        // Verify that the instance is not null
        assertNotNull(instance, "Instance should not be null");
    }
}

