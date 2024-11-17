package sys.tem;

import sys.tem.client.Client;
import sys.tem.setting.Config;

public class ScriptClient2 {
    public static void main(String[] args) {
        Client client = new Client(new Config("resources/setting/Setting.conf")).started();
    }
}
