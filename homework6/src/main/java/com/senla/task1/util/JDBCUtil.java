package com.senla.task1.util;

import com.senla.task1.annotations.ConfigProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {

    private static JDBCUtil instance;
    @ConfigProperty(propertyName = "JDBCUtil.datasource.url")
    private static String jdbcUrl;
    @ConfigProperty(propertyName = "JDBCUtil.datasource.username")
    private static String username;
    @ConfigProperty(propertyName = "JDBCUtil.datasource.password")
    private static String password;

    public JDBCUtil() {
    }

    public static JDBCUtil getInstance() {
        if (instance == null) {
            instance = new JDBCUtil();
        }
        return instance;
    }

    public Connection connect() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД", e);
        }
    }
}
