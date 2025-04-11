package hr.java.scrabble.config;

import hr.java.scrabble.config.jndi.ConfigurationKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface ConfigReader {
    String CONFIG_PROPERTIES = "conf.properties";

    static String getValue(ConfigurationKey key) {
        try (InputStream input = ClassLoader.getSystemResourceAsStream(CONFIG_PROPERTIES)) {
            if (input == null) {
                throw new IllegalArgumentException("File not found: " + CONFIG_PROPERTIES);
            }
            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty(key.getKey());
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file: " + CONFIG_PROPERTIES, e);
        }
    }

}
