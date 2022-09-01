package client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import server.Server;

import java.io.IOException;


class ClientTest {

    @Test
    public void connectAndDisconnectFromTheServer() throws IOException {
        Thread server = new Thread(Server::serverStart);
        server.setDaemon(true);
        server.start();

        Client client = Mockito.spy(Client.class);

        MockedStatic<?> mockedStatic = Mockito.mockStatic(ClientTextReader.class);
        mockedStatic.when(ClientTextReader::readLine).thenReturn("exit");
        Mockito.doReturn("Billi").when(client).getClientName();

        Assertions.assertDoesNotThrow(client::run);
    }
}