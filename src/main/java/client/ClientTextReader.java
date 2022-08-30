package client;

import java.util.Scanner;

public class ClientTextReader {
    private static Scanner scan = new Scanner(System.in);


    public static String readLine() {
        return scan.nextLine();
    }
}
