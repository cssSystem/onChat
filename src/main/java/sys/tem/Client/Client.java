package sys.tem.Client;

import sys.tem.Setting.Config;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Scanner scanner;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread connect;

    public Socket getSocket() {
        return socket;
    }

    public Client(Config config) {
        scanner = new Scanner(System.in);
        connect = new Thread(() -> {
            while (true) {
                try {
                    socket = new Socket(config.HOST, config.PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    scanner = new Scanner(System.in);

                    Thread messageReceiver = new Thread(() -> {
                        String inMess;
                        while ((inMess = getMessage()) != null) {
                            System.out.println(inMess);
                        }
                    });
                    messageReceiver.start();

                    Thread senderMessages = new Thread(() -> {
                        String msg;
                        while (!(msg = scanner.nextLine()).equals(config.KILL_COMAND)) {
                            setMessage(msg);
                        }
                        System.out.println("Вы отключились...");
                        out.close();
                        try {
                            in.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        messageReceiver.stop();
                    });
                    senderMessages.start();

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
        connect.start();

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
