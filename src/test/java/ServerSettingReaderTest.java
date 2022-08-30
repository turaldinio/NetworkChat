import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.ServerSettingReader;


class ServerSettingReaderTest {

    @Test
    void readStringAndIntegerKey() {
        String host = ServerSettingReader.readStringKey("host");
        Assertions.assertNotNull(host);

        int port = ServerSettingReader.readIntKey("port");
        Assertions.assertNotEquals(0,port);

    }

}