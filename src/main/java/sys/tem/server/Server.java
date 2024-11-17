package sys.tem.server;

import sys.tem.log.Logger;
import sys.tem.setting.Config;

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
    private boolean started = false;
    private List<Call> calling;
    private final Config config;
    private ServerSocket SERVER;
    private final Thread SERVER_ACCEPT;
    private final Logger LOG;

    public List<Call> getCalling() {
        return calling;
    }

    public boolean isClosed() {
        return SERVER.isClosed();
    }

    public Server closed() {
        started = false;
        try {
            SERVER.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SERVER_ACCEPT.stop();
        return this;
    }

    public Server started() {
        SERVER_ACCEPT.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public Server(Config config, Logger log) {
        this.config = config;
        this.LOG = log;
        SERVER_ACCEPT = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(config.PORT);) {
                started = true;
                this.SERVER = server;
                calling = Collections.synchronizedList(new ArrayList<>());
                LOG.logToFile(LOG.logString("Сервер запущен...", Logger.typEnum.INFO));

                while (started) {
                    final Socket socket;
                    socket = server.accept();
                    Call thread = new Call(socket);
                    calling.add(thread);
                    thread.start();
                }
            } catch (IOException e) {
                LOG.logToFile(LOG.logString("Сервер остановлен...\n" + e.toString(), Logger.typEnum.ERROR));
                started = false;
            }
        });


    }

    public class Call extends Thread {
        private final Socket SOCKET;
        private BufferedReader in;
        private PrintWriter out;
        private String nickName;
        private boolean started = false;

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
                 PrintWriter out = new PrintWriter(SOCKET.getOutputStream(), true);) {
                this.in = in;
                this.out = out;
                started = true;

                String textMsg;
                while (started) {
                    if (nickName == null) {
                        String nick = nickName();
                        nickName = nick;
                        sendMsg("Добро пожаловать \"" + nick + "\". Вы в CHATе...");
                        LOG.logToFile(LOG.logString("Сокет " + SOCKET.toString() + " присвоено имя: " + nickName, Logger.typEnum.SERVER_TEXT));
                        logSendMsgAll("К чату подключился пользователь \"" + nickName + "\"", Logger.typEnum.SERVER_TEXT);

                    }
                    textMsg = acceptMsg();
                    if (textMsg != null) {
                        logSendMsgAll(resTemp(textMsg), Logger.typEnum.CLIENT_TEXT);
                    } else {
                        LOG.logToFile(LOG.logString(nickName == null ? "Сокет " + SOCKET.toString() + " отключился" : nickName + " отключился...", Logger.typEnum.SERVER_TEXT));
                        if (nickName != null) {
                            textMsg = nickName + " ушел погулять в оффлайн";
                            logSendMsgAll(textMsg, Logger.typEnum.CLIENT_TEXT);
                        }
                        calling.remove(Thread.currentThread());
                        started = false;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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
                return msg;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Call(Socket socket) {
            this.SOCKET = socket;
        }

        private void sendMsgAll(String msg) {
            synchronized (calling) {
                calling.forEach(call -> {
                    call.sendMsg(msg);
                });
            }
        }
    }


}
