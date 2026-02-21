package com.senla.task1.util;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseUtil {
    private static final String URL = AppProperties.getProperty("hibernate.datasource.url");
    private static final String USERNAME = AppProperties.getProperty("hibernate.datasource.username");
    private static final String PASSWORD = AppProperties.getProperty("hibernate.datasource.password");


    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void liquibaseUpdate() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            try {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                Liquibase liquibase = new liquibase.Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts(), new LabelExpression());
            } catch (LiquibaseException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
