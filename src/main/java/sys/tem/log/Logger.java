package sys.tem.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Logger {
    public enum typEnum {
        INFO,
        ERROR,
        SERVER_TEXT,
        CLIENT_TEXT,
        UNKNOWN
    }

    private Logger.typEnum typEn;
    private char separator = " ".charAt(0);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Logger logger;
    private String PATCH;

    public String getPATCH() {
        return PATCH;
    }

    private Logger() {
    }

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void logToConsole(String logMsg) {
        System.out.println(logMsg);
    }

    public String logString(String logMsg, typEnum type) {
        StringBuilder sB = new StringBuilder();
        switch (type) {
            case INFO:
                sB.append("INFO");
                break;
            case ERROR:
                sB.append("ERROR");
                break;
            case SERVER_TEXT:
                sB.append("ServerTEXT");
                break;
            case CLIENT_TEXT:
                sB.append("ClientTEXT");
                break;
            default:
                sB.append("UNKNOWN");
        }
        sB.append(separator);
        sB.append(LocalDateTime.now().format(formatter));
        sB.append(separator);
        sB.append(logMsg);
        sB.append("\n");
        return sB.toString();
    }

    public void setPATCH(String PATCH) {
        this.PATCH = PATCH;
    }

    public boolean logToFile(String logMsg, String PATCH) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(PATCH, true));
            printWriter.write(logMsg);
            printWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();//Ошибка при обращении к файлу
            return false;
        }
    }

    public boolean logToFile(String logMsg) {
        if (PATCH == null) {
            return false;
        }
        return logToFile(logMsg, PATCH);
    }
}
