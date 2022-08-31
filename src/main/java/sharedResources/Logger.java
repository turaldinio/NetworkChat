package sharedResources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Logger {
    private static FileWriter fileWriter;
    private static File file = new File("src/main/java/sharedResources/logFile.log");

    public static void log(String line, String importanceLevel) {
        if (fileWriter == null) {
            fileInit();
        }
        try {
            System.out.println(line);
            fileWriter.write(String.format("[%1$ty-%1$tm-%1$td %1$tH:%1$tM:%1$tS %2$s] %3$s\n", LocalDateTime.now(), line, importanceLevel));
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println("ошибка записи в файл ");
        }
    }

    private static void fileInit() {
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            System.out.println("ошибка создания FileReader Logger->fileInit()");
        }
    }

    public static void clearTheFile() {
        file.delete();

    }
}
