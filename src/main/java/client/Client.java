package client;

import sharedResources.*;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private boolean clientConnected = false;

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SocketThread extends Thread {
        @Override
        public void run() {
            try {
                Socket socket = new Socket(SettingReader.readStringKey("host"), SettingReader.readIntKey("port"));
                Client.this.connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();

            } catch (IOException e) {
                notifyConnectionStatusChanged(false);
            } catch (ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);

                e.printStackTrace();
            }
        }

        protected void processIncomingMessage(String message) {
            System.out.println(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            Logger.log("CLIENT: Участник " + userName + " " + "присоединился к чату", "INFO");
        }

        protected void informAboutDeletingNewUser(String userName) {
            Logger.log("CLIENT: Участник " + userName + " " + "покинул чат", "INFO");

        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                switch (message.getMessageType()) {
                    case NAME_REQUEST:
                        String userName = getClientName();
                        connection.send(new Message(userName, MessageType.USER_NAME));
                        break;
                    case NAME_ACCEPTED:
                        notifyConnectionStatusChanged(true);
                        return;
                }

            }
        }

        private void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                switch (message.getMessageType()) {
                    case TEXT:
                        processIncomingMessage(message.getText());
                        break;
                    case USER_ADDED:
                        informAboutAddingNewUser(message.getText());
                        break;
                    case USER_REMOVED:
                        informAboutDeletingNewUser(message.getText());
                        break;
                }
            }
        }

    }

    public void run() throws IOException {
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
            Logger.log("CLIENT: Соединение установлено Для выхода введите exit", "INFO");

        } else {
            Logger.log("CLIENT: произошла ошибка в работе потока SocketThread->run", "ERROR");

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
        System.out.println("Введите имя клиента");

        return ClientTextReader.readLine();
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) throws IOException {
        connection.send(new Message(text, MessageType.TEXT));
    }

}
