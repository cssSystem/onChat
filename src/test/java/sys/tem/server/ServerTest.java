package sys.tem.server;

import org.junit.jupiter.api.*;
import sys.tem.client.Client;
import sys.tem.log.Logger;
import sys.tem.setting.Config;

public class ServerTest {
    Server server = new Server(new Config("resources/setting/Setting.conf"), Logger.getInstance());

    @BeforeEach
    public void bef() {
        Logger.getInstance().setPATCH("resources/Log.log");
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
        Client client = new Client(new Config("resources/setting/Setting.conf"));
        client.started();
        Thread.sleep(500);
        client.setMessage(msg);
        Thread.sleep(500);
        Assertions.assertEquals(msg, server.getCalling().get(0).getNickName());
    }
}
