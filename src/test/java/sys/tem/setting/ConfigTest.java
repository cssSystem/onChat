package sys.tem.setting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigTest {
    private String patch = "resources/setting/Setting.conf";

    @Test
    void configTest() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            new Config("");
        });
    }

    @Test
    void getPatch() {
        Config config = new Config(patch);
        Assertions.assertEquals(patch, config.PATCH);
    }
}
