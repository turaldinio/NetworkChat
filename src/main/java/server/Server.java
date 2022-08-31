package server;

import sharedResources.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        Logger.clearTheFile();

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
                Logger.log("SERVER: Ошибка отправки сообщения. Server->sendBroadcastMessage", "ERROR");
            }
        });
    }

    private static class Handler extends Thread {
        private Socket socket;

        @Override
        public void run() {
            super.run();
            Logger.log("SERVER: Установленно соединение с адресом " + socket.getRemoteSocketAddress(), "INFO");

            String userName = null;

            try (Connection connection = new Connection(socket)) {
                Logger.log("SERVER: Подключение к порту " + connection.getRemoteSocketAddress(), "INFO");

                userName = serverHandshake(connection);

                sendBroadcastMessage(new Message(userName, MessageType.USER_ADDED));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);

            } catch (IOException | ClassNotFoundException e) {
                Logger.log("SERVER: Ошибка при обмене данными с удаленным адресом", "ERROR");
            }

            if (userName != null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(userName, MessageType.USER_REMOVED));
            }
            Logger.log("SERVER: Соединение с удаленным адресом закрыто", "INFO");


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
                    Logger.log("SERVER: Ошибка отправки сообщения sendListOfUsers()", "ERROR");
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
                    Logger.log("Server: Ошибка принятого сообщения serverMainLoop()", "ERROR");

                }
            }
        }
    }
}