package sys.tem;

import sys.tem.Log.Logger;
import sys.tem.Setting.Config;
import sys.tem.Server.Server;

import java.io.IOException;

public class ScriptServer {
    public static void main(String[] args) {
        Logger.getInstance().setPATCH("src/main/java/sys/tem/Log/log.log");
        Server server = new Server(new Config("src/main/java/sys/tem/Setting/Setting.conf"), Logger.getInstance()).started();
    }

}
