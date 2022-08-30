import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
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

    }
}
