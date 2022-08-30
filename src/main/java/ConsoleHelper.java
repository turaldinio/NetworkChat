import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Ошибка при выводе сообщения ");
            return null;
        }
    }

    public static int readInt() {
        try {
            return Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            System.out.println("Ошибка при выводе сообщения");
        return 0;
        }
    }
}
