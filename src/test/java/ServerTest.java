import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {


    @Test
    public void serverStart() {
        try {
            ServerSocket server = new ServerSocket(ServerSettingReader.readIntKey("port"));
            Thread.sleep(200);
            new Thread(() -> {
                try {
                   new Socket(ServerSettingReader.readStringKey("host"), ServerSettingReader.readIntKey("port"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            Socket socket = server.accept();
            Assertions.assertNotNull(socket);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
