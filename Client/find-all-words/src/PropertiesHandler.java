import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {
    private static final Properties configProperties = new Properties();

    public static void loadProperties() {
        try {
            configProperties.load(new FileInputStream("Client/find-all-words/resources/config.properties"));
        } catch (IOException e2) {
            e2.printStackTrace();

        }
        PropertiesHandler.setProperty("serverAddress", PropertiesHandler.getProperty("defaultAddress"));
        PropertiesHandler.setProperty("serverPort", PropertiesHandler.getProperty("defaultPort"));
    }

    public static String getProperty(String key) {
        return configProperties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        configProperties.setProperty(key, value);

    }

    public static void saveProperties() {
        try {
            configProperties.store(new FileWriter("Client/find-all-words/resources/config.properties"), "saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
