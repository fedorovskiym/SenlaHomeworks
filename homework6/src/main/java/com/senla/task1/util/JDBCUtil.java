package com.senla.task1.util;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {

    private static JDBCUtil instance;
    @Value("${JDBCUtil.datasource.url}")
    private static String jdbcUrl;
    @Value("${JDBCUtil.datasource.username}")
    private static String username;
    @Value("${JDBCUtil.datasource.password}")
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
