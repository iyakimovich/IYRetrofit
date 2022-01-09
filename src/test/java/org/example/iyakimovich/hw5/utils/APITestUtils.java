package org.example.iyakimovich.hw5.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.example.iyakimovich.hw5.dto.ProductDTO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Properties;

@UtilityClass
public class APITestUtils {
    Properties props = new Properties();
    FileInputStream fis;

    static {
        try {
            fis = new FileInputStream("src/test/resources/api.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @SneakyThrows
    public String getBaseUrl() {
        props.load(fis);
        return props.getProperty("url");
    }
}
