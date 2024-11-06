package sys.tem;

import sys.tem.Client.Client;
import sys.tem.Setting.Config;

public class scriptClient2 {
    public static void main(String[] args) {
        Client client = new Client(new Config("src/main/java/sys/tem/Setting/Setting.conf"));
    }
}
