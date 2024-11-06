package sys.tem.Setting;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public Config(String PATCH) {
        try (FileReader fileReader = new FileReader(PATCH);) {
            Properties properties = new Properties();
            properties.load(fileReader);
            this.PATCH = PATCH;
            this.PORT = Integer.parseInt(properties.getProperty("PORT"));
            this.HOST = properties.getProperty("HOST");
            this.KILL_COMAND = properties.getProperty("KILLCOMAND");
        } catch (IOException e) {
            System.out.println("Файл настроек недоступен");
            throw new RuntimeException(e);
        }

    }


    public final String PATCH;
    public final int PORT;
    public final String HOST;
    public final String KILL_COMAND;

}
