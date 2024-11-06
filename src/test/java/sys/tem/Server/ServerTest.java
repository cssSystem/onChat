package sys.tem.Server;

import org.junit.jupiter.api.*;
import sys.tem.Client.Client;
import sys.tem.Log.Logger;
import sys.tem.Setting.Config;

public class ServerTest {
    Server server = new Server(new Config("src/main/java/sys/tem/Setting/Setting.conf"), Logger.getInstance());

    @BeforeEach
    public void bef() {
        Logger.getInstance().setPATCH("src/main/java/sys/tem/Log/log.log");
    }

    @Test
    public void serverClosedTest() {
        server.started();
        server.closed();
        Assertions.assertEquals(true, server.isClosed());
    }

    @Test
    public void serverMessTest() throws InterruptedException {
        server.started();
        String msg = "hello world";
        Client client = new Client(new Config("src/main/java/sys/tem/Setting/Setting.conf"));
        Thread.sleep(500);
        client.setMessage(msg);
        Thread.sleep(500);
        Assertions.assertEquals(msg, server.getCalling().get(0).getNickName());
    }
}
