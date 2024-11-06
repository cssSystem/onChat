package sys.tem.Setting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigTest {
    private String Patch = "src/main/java/sys/tem/Setting/Setting.conf";

    @Test
    void ConfigTest() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            new Config("");
        });
    }

    @Test
    void getPatch() {
        Config config = new Config(Patch);
        Assertions.assertEquals(Patch, config.PATCH);
    }
}
