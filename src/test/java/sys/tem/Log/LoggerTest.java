package sys.tem.Log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoggerTest {
    Logger logger = Logger.getInstance();

    @Test
    void getInstanceTest() {
        Assertions.assertEquals(logger, Logger.getInstance());
    }

    @Test
    void logToFileTest() {
        Assertions.assertAll("Test",
                () -> Assertions.assertFalse(logger.logToFile(""), "Test no PATCH"),
                () -> Assertions.assertNull(logger.getPATCH(), "Test PATCH Null")
        );

        logger.setPATCH("patch");
        Assertions.assertEquals("patch", logger.getPATCH());
    }
}
