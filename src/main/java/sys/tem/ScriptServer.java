package sys.tem;

import sys.tem.log.Logger;
import sys.tem.setting.Config;
import sys.tem.server.Server;

public class ScriptServer {
    public static void main(String[] args) {
        Logger.getInstance().setPATCH("resources/Log.log");
        Server server = new Server(new Config("resources/setting/Setting.conf"), Logger.getInstance()).started();
    }

}
