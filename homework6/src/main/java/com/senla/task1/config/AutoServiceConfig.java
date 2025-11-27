package com.senla.task1.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AutoServiceConfig {

    private boolean allowAddGaragePlace;
    private boolean allowDeleteGaragePlace;
    private boolean allowShiftOrdersTime;
    private boolean allowDeleteOrder;

    public AutoServiceConfig() {
        load();
    }

    public void load() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("autoservice.properties")) {

            properties.load(inputStream);

            allowAddGaragePlace = Boolean.parseBoolean(properties.getProperty("allowAddGaragePlace"));
            allowDeleteGaragePlace = Boolean.parseBoolean(properties.getProperty("allowDeleteGaragePlaces"));
            allowShiftOrdersTime = Boolean.parseBoolean(properties.getProperty("allowShiftOrdersTime"));
            allowDeleteOrder = Boolean.parseBoolean(properties.getProperty("allowDeleteOrder"));

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла конфигурации", e);
        }
    }

    public boolean isAllowAddGaragePlace() {
        return allowAddGaragePlace;
    }

    public boolean isAllowDeleteGaragePlace() {
        return allowDeleteGaragePlace;
    }

    public boolean isAllowShiftOrdersTime() {
        return allowShiftOrdersTime;
    }

    public boolean isAllowDeleteOrder() {
        return allowDeleteOrder;
    }
}
