package put.poznan.tools;

import java.io.*;
import java.util.Properties;

public class PropertiesHandler {
    private static final Properties configProperties = new Properties();

    public static void loadProperties() {
        try {
            configProperties.load(new FileInputStream("Client/find-all-words/resources/config.properties"));
        } catch (IOException e2) {
            try {
                new FileOutputStream("Client/find-all-words/resources/config.properties");
                configProperties.load(new FileInputStream("Client/find-all-words/resources/config.properties"));

                configProperties.setProperty("defaultPort", "1313");
                configProperties.setProperty("defaultAddress", "127.0.0.1");
                configProperties.setProperty("about", "TODO");

            } catch (IOException e) {
                e.printStackTrace();
            }

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
