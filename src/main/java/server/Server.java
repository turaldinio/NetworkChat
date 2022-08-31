package server;

import sharedResources.Connection;
import sharedResources.Message;
import sharedResources.MessageType;
import sharedResources.SettingReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(SettingReader.readIntKey("port"))) {


            while (true) {
                //Listen
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                Handler handler = new Handler(socket);

                handler.start();
            }
        }


    }

    public static void sendBroadcastMessage(Message message) {
        connectionMap.values().forEach(x -> {
            try {
                x.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static class Handler extends Thread {
        private Socket socket;

        @Override
        public void run() {
            super.run();

            System.out.println("Установленно соединение с адресом " + socket.getRemoteSocketAddress());
            String userName = null;

            try (Connection connection = new Connection(socket)) {
                System.out.println("Подключение к порту: " + connection.getRemoteSocketAddress());
                userName = serverHandshake(connection);

                sendBroadcastMessage(new Message(userName, MessageType.USER_ADDED));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);

            } catch (IOException e) {
                System.out.println("Ошибка при обмене данными с удаленным адресом");
            } catch (ClassNotFoundException e) {
                System.out.println("Ошибка при обмене данными с удаленным адресом");
                e.printStackTrace();
            }

            if (userName != null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(userName, MessageType.USER_REMOVED));
            }
            System.out.println("Соединение с удаленным адресом закрыто");


        }

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();

                if (message.getMessageType() == MessageType.USER_NAME) {

                    if (!message.getText().isEmpty()) {

                        if (connectionMap.get(message.getText()) == null) {

                            connectionMap.put(message.getText(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));

                            return message.getText();
                        }
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) {
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                if (pair.getKey().equals(userName))
                    break;

                try {
                    connection.send(new Message(pair.getKey(), MessageType.USER_ADDED));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {

                Message message = connection.receive();
                if (message.getMessageType() == MessageType.TEXT) {

                    String s = userName + ": " + message.getText();

                    Message formattedMessage = new Message(s, MessageType.TEXT);
                    sendBroadcastMessage(formattedMessage);
                } else {
                    System.out.println("error");
                }
            }
        }
    }
}