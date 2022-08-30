import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(ServerSettingReader.readIntKey("port"));
            System.out.println("Сервер запущен");

            while (true) {
                Socket client = server.accept();

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


        }

        private String serverHandshake(Connection connection) {
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

        private void sendListOfUsers(Connection connection, String userName) {
            for (Map.Entry<String, Connection> pairs : map.entrySet()) {
                String oldClientMane = pairs.getValue().receive().getText();
                if (!oldClientMane.equals(userName)) {
                    connection.send(new Message(oldClientMane, MessageType.USER_ADDED));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) {
            while (true) {
                Message message = connection.receive();
                if (message.getMessageType() == MessageType.TEXT) {
                    String newMessage = userName + " : " + message.getText();
                    sendMessageToEveryOne(new Message(newMessage, MessageType.TEXT));
                } else {
                    System.out.println("Ошибка, сообщение не является текстом");
                }
            }
        }

    }

    public static void sendMessageToEveryOne(Message message) {
        map.values().forEach(x -> x.send(message));
    }
}
