package sys.tem.Server;

import sys.tem.Log.Logger;
import sys.tem.Setting.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private List<Call> calling;
    final Config config;
    private ServerSocket SERVER;
    private final Thread SERVER_ACCEPT;
    final Logger LOG;

    public List<Call> getCalling() {
        return calling;
    }

    public boolean isClosed() {
        return SERVER.isClosed();
    }

    public Server closed() {
        try {
            SERVER_ACCEPT.stop();
            SERVER.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public Server started() {
        try {
            this.SERVER = new ServerSocket(config.PORT);
            SERVER_ACCEPT.start();
            calling = Collections.synchronizedList(new ArrayList<>());

            LOG.logToFile(LOG.logString("Сервер запущен...", Logger.typEnum.INFO));
        } catch (IOException e) {
            LOG.logToFile(LOG.logString("Сервер остановлен\n" + e.toString(), Logger.typEnum.ERROR));
        }
        return this;
    }

    public Server(Config config, Logger log) {
        this.config = config;
        this.LOG = log;
        SERVER_ACCEPT = new Thread(() -> {
            while (true) {
                final Socket socket;
                try {
                    socket = SERVER.accept();
                    Call thread = new Call(socket);
                    calling.add(thread);
                    thread.start();
                } catch (IOException e) {
                    LOG.logToFile(LOG.logString("Сервер остановлен...\n" + e.toString(), Logger.typEnum.ERROR));
                    Thread.currentThread().stop();
                }

            }
        });


    }

    public class Call extends Thread {
        final Socket SOCKET;
        private final BufferedReader in;
        private final PrintWriter out;
        private String nickName;

        @Override
        public void run() {
            String textMsg;
            while (true) {
                if (nickName == null) {
                    String nick = nickName();
                    nickName = nick;
                    sendMsg("Добро пожаловать \"" + nick + "\". Вы в CHATе...");
                    LOG.logToFile(LOG.logString("Сокет " + SOCKET.toString() + " присвоено имя: " + nickName, Logger.typEnum.SERVER_TEXT));
                    logSendMsgAll("К чату подключился пользователь \"" + nickName + "\"", Logger.typEnum.SERVER_TEXT);

                }
                textMsg = acceptMsg();
                logSendMsgAll(resTemp(textMsg), Logger.typEnum.CLIENT_TEXT);
            }
        }

        private void logSendMsgAll(String s, Logger.typEnum i) {
            LOG.logToFile(LOG.logString(s, i));
            sendMsgAll(s);
        }

        private void logSendMsg(String s, Logger.typEnum i) {
            LOG.logToFile(LOG.logString(s, i));
            sendMsg(s);
        }

        private String resTemp(String textMsg) {
            return nickName + ": " + textMsg;
        }

        public String getNickName() {
            return nickName;
        }

        private String nickName() {
            String nick;
            AtomicBoolean un = new AtomicBoolean(false);
            LOG.logToFile(LOG.logString("Сокет " + SOCKET.toString() + " подключился", Logger.typEnum.SERVER_TEXT));
            while (true) {
                sendMsg("Для участия укажите ваш ник (Ник должен быть уникальным):");
                nick = acceptMsg();
                un.set(true);
                synchronized (calling) {
                    String finalNick = nick;
                    calling.forEach(call -> {
                        if (Objects.equals(call.nickName, finalNick)) {
                            sendMsg("Ник \"" + finalNick + "\" не уникален.");
                            un.set(false);
                        }
                    });
                }
                if (!un.get()) {
                    continue;
                }
                break;
            }
            return nick;
        }

        public void sendMsg(String msg) {
            out.println(msg);
        }

        private String acceptMsg() {
            try {
                String msg = in.readLine();
                if (msg == null) {
                    LOG.logToFile(LOG.logString(nickName == null ? "Сокет " + SOCKET.toString() + " отключился" : nickName + " отключился...", Logger.typEnum.SERVER_TEXT));
                    in.close();
                    out.close();
                    SOCKET.close();
                    calling.remove(Thread.currentThread());
                    Thread.currentThread().stop();
                }
                return msg;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Call(Socket socket) {
            this.SOCKET = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        private void sendMsgAll(String msg) {
            synchronized (calling) {
                calling.forEach(call -> {
//                    if (call.nickName != nickName) {
//                        call.sendMsg(msg);
//                    }
                    call.sendMsg(msg);
                });
            }
        }
    }


}
