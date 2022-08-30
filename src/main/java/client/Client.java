package client;

import sharedResources.Connection;
import sharedResources.Message;
import sharedResources.MessageType;

import java.util.Scanner;

public class Client {
    protected Connection connection;
    private boolean clientConnected = false;

    public class SocketThread extends Thread {

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
