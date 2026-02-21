package com.senla.task1.util;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class AppProperties {
    private static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private static String appConfigPath = rootPath + "autoservice.properties";

    private static Properties appProps = new Properties();

    public static String getProperty(String propertyName) {

        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return appProps.getProperty(propertyName);
    }
}
