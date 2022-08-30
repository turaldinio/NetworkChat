import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sharedResources.SettingReader;


class ServerSettingReaderTest {

    @Test
    void readStringAndIntegerKey() {
        String host = SettingReader.readStringKey("host");
        Assertions.assertNotNull(host);

        int port = SettingReader.readIntKey("port");
        Assertions.assertNotEquals(0,port);

    }

}