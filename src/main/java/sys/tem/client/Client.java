package sys.tem.client;

import sys.tem.setting.Config;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Config config;
    private Scanner scanner;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean start = false;
    private Thread messageReceiver = new Thread(() -> {
        String inMess;
        while ((inMess = getMessage()) != null) {
            System.out.println(inMess);
        }
        System.out.println("Сервер отключен.");
    });
    private Thread senderMessages = new Thread(() -> {
        String msg;
        while (!(msg = scanner.nextLine()).equals(config.KILL_COMAND)) {
            setMessage(msg);
        }
        System.out.println("Вы отключились...");
        start = false;
    });
    private Thread connect = new Thread(() -> {
        while (true) {
            try (Socket socket = new Socket(config.HOST, config.PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 Scanner scanner = new Scanner(System.in);) {
                this.socket = socket;
                this.in = in;
                this.out = out;
                this.scanner = scanner;


                messageReceiver.start();

                senderMessages.start();
                while (start) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                System.out.println("Сервер недоступен. " +
                        "Повтор подключения через:");
                for (int i = 3; i >= 0; i--) {
                    System.out.print(i + "\r");
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                continue;
            }

            break;
        }

    });

    public Socket getSocket() {
        return socket;
    }


    public Client stoped() {
        start = false;
        return this;
    }

    public Client started() {
        start = true;
        connect.start();
        return this;
    }

    public Client(Config config) {
        this.config = config;
    }

    public boolean setMessage(String msg) {
        if (out != null) {
            out.println(msg);
            return true;
        }
        return false;
    }

    public String getMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

}
