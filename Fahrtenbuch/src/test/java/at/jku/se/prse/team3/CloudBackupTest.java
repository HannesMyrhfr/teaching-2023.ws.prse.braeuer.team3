package at.jku.se.prse.team3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class CloudBackupTest {

    private static final String TEST_LOCAL_PATH = "testfile.txt";
    private static final String TEST_CLOUD_PATH = "/test/testfile.txt";
    private static final String TEST_ACCESS_TOKEN = "sl.BtqEBYEbnssdpPERdV8fBWED3MzmjosS02JQBeRsTTi4ii0kLltnz3f0tjNVpOxoG6oZKlSbHYcqOMbNh6eB0J1UioaVvIWps-pAsELHZzXR666jIjwKt6J7jdGQNMmbaZ4kCAcIYmANvZw";

    @BeforeAll
    static void setup() {
        try {
            File testFile = new File(TEST_LOCAL_PATH);
            if (!testFile.exists() && testFile.createNewFile()) {
                System.out.println("Test file created successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Only run the following test this if a current token is available !!!
    /*
    @Test
    void testUploadDB_WithValidInputs_ShouldUploadFile() {
        assertDoesNotThrow(() -> CloudBackup.uploadDB(TEST_LOCAL_PATH, TEST_CLOUD_PATH, TEST_ACCESS_TOKEN));
    }
    */

    @Test
    void testUploadDB_WithInvalidAccessToken_ShouldThrowRuntimeException() {

        String invalidAccessToken = "invalid-access-token";
        assertThrows(CloudBackup.FileUploadException.class, () -> CloudBackup.uploadDB(TEST_LOCAL_PATH, TEST_CLOUD_PATH, invalidAccessToken));
    }
}
