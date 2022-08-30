package client;

import sharedResources.Connection;
import sharedResources.Message;
import sharedResources.MessageType;

import java.util.Scanner;

public class Client {
    protected Connection connection;
    private boolean clientConnected = false;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message) {
            System.out.println(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            System.out.println("Участник " + userName + " " + "присоединился к чату");
        }

        protected void informAboutDeletingNewUser(String userName) {
            System.out.println("Участник " + userName + " " + "покинул чат");

        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (clientConnected) {
            System.out.println("Соединение установлено\n Для выхода введите eixt");
        } else {
            System.out.println("произошла ошибка в работе потока SocketThread->run");
        }
        while (clientConnected) {
            String line = ClientTextReader.readLine();
            if (line.equals("exit")) {
                break;
            }
            sendTextMessage(line);
        }

    }

    protected String getClientName() {
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        connection.send(new Message(text, MessageType.TEXT));
    }

}
