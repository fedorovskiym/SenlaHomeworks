package com.senla.task1.dao;

import com.senla.task1.util.JDBCUtil;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDAOImpl<T extends Serializable, ID> implements GenericDAO<T, ID> {

    private Connection connection;

    protected abstract String getTableName();

    protected abstract String getInsertSql();

    protected abstract String getUpdateSql();

    protected abstract void fillPreparesStatementForInsert(PreparedStatement preparedStatement, T entity) throws SQLException;

    protected abstract void fillPreparesStatementForUpdate(PreparedStatement preparedStatement, T entity) throws SQLException;

    protected abstract T mapRow(ResultSet resultSet) throws SQLException;

    public Connection getConnection() {
        if (connection == null) {
            connection = JDBCUtil.getInstance().connect();
        }
        return connection;
    }

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                T entity = mapRow(resultSet);
                entities.add(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entities;
    }

    @Override
    public void save(T entity) {
        String sql = getInsertSql();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            fillPreparesStatementForInsert(preparedStatement, entity);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, (Integer) id);
            int deletedRows = preparedStatement.executeUpdate();
            if (deletedRows == 0) {
                throw new RuntimeException("Сущность с id = " + id + " не найдена");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(T entity) {
        String sql = getUpdateSql();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)){
            fillPreparesStatementForUpdate(preparedStatement, entity);
            int updatedRows = preparedStatement.executeUpdate();
            if(updatedRows == 0) {
                throw new RuntimeException("Сущность не была обновлена");
            } else {
                System.out.println("Сущность была обновлена");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
