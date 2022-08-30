package sharedResources;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SettingReader {
    private  static Properties properties;

    public static String readStringKey(String key) {
        if (properties == null) {
            propertyInit();
        }
        return properties.getProperty(key);
    }

    public static int readIntKey(String key) {
        if (properties == null) {
            propertyInit();
        }
        return Integer.parseInt(properties.getProperty(key));
    }

    private static void propertyInit() {
        properties = new Properties();
        try {
            properties.load(new FileReader("src/main/java/sharedResources/serverSettings.property"));
        } catch (IOException e) {
            System.out.println("Указанный файл не найден");
        }

    }
}
