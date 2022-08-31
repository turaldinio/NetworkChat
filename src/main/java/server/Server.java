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
    private static Map<String, Connection> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(SettingReader.readIntKey("port"))) {
            System.out.println("Сервер запущен");

            while (true) {
                Socket client = null;
                try {
                    client = server.accept();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    break;
                }

                Handler handler = new Handler(client);
                handler.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            String newUserName;

            try (Connection connection = new Connection(socket)) {
                System.out.println("Установлено соединение " + connection.getRemoteSocketAddress());
                newUserName = serverHandshake(connection);
                sendMessageToEveryOne(new Message(newUserName, MessageType.USER_ADDED));
                sendListOfUsers(connection, newUserName);

                serverMainLoop(connection, newUserName);
            } catch (Exception e) {
                System.out.println("ошибка в методе Handler->run");
                e.printStackTrace();
                return;
            }
            if (newUserName != null) {
                map.remove(newUserName);
                sendMessageToEveryOne(new Message(newUserName, MessageType.USER_REMOVED));
            }
            System.out.println("Соединение с " + newUserName + " закрыто");

        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));

                Message response = connection.receive();

                if (response.getMessageType() == MessageType.USER_NAME && response.getText() != null && !map.containsKey(response.getText())) {

                    map.put(response.getText(), connection);

                    connection.send(new Message(MessageType.NAME_ACCEPTED));
                    return response.getText();
                }


            }


        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pairs : map.entrySet()) {
                if (!pairs.getKey().equals(userName)) {
                    connection.send(new Message(pairs.getKey(), MessageType.USER_ADDED));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getMessageType() == MessageType.TEXT) {
                    String newMessage = userName + " : " + message.getText();
                    sendMessageToEveryOne(new Message(newMessage, MessageType.TEXT));
                } else {
                    System.out.println("Ошибка, сообщение не является текстом");
                    return;
                }
            }
        }

    }

    public static void sendMessageToEveryOne(Message message) {
        map.values().forEach(x -> {
            try {
                x.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
