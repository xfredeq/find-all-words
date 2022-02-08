package tools;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;

public class PropertiesHandler {
    private static final Properties configProperties = new Properties();
    private static File file;

    public static void loadProperties() {
        try {
            file = Files.createTempFile("config", "properties").toFile();
            OutputStream stream = new FileOutputStream(file);
            stream.write(Objects.requireNonNull(PropertiesHandler.class.getClassLoader()
                    .getResourceAsStream("config.properties")).readAllBytes());
            stream.close();
            System.out.println(file.getAbsolutePath());
            configProperties.load(new FileInputStream(file));
        } catch (IOException e) {
            System.err.println("No properties File");
            System.exit(-1);
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
            configProperties.store(new FileWriter(file), "saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
