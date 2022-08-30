import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sharedResources.SettingReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {


    @Test
    public void serverStart() {
        try {
            ServerSocket server = new ServerSocket(SettingReader.readIntKey("port"));
            Thread.sleep(200);
            new Thread(() -> {
                try {
                   new Socket(SettingReader.readStringKey("host"), SettingReader.readIntKey("port"));
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
