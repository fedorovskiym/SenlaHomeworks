package com.senla.task1.util;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class LiquibaseUtil {
    @Value("${JDBCUtil.datasource.url}")
    private String url;
    @Value("${JDBCUtil.datasource.username}")
    private String username;
    @Value("${JDBCUtil.datasource.password}")
    private String password;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//    public void liquibaseUpdate() {
//        System.out.println(url);
//        try (Connection connection = DriverManager.getConnection(url, username, password)) {
//            try {
//                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
//                Liquibase liquibase = new liquibase.Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
//                liquibase.update(new Contexts(), new LabelExpression());
//            } catch (LiquibaseException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
