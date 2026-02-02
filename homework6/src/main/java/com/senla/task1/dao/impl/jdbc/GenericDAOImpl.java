package com.senla.task1.dao.impl.jdbc;

import com.senla.task1.dao.GenericDAO;
import com.senla.task1.util.JDBCUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAOImpl<T, PK extends Serializable> implements GenericDAO<T, PK> {

    private static final Logger logger = LogManager.getLogger(GenericDAOImpl.class);
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
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                T entity = mapRow(resultSet);
                entities.add(entity);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при нахождении сущностей", e);
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
            logger.error("Ошибка при сохранении сущности", e);
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public void delete(T entity) {
//        String sql = "DELETE FROM " + getTableName() + " WHERE id=?";
//        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
//            preparedStatement.setInt(1, T.);
//            int deletedRows = preparedStatement.executeUpdate();
//            if (deletedRows == 0) {
//                throw new RuntimeException("Сущность с id = " + id + " не найдена");
//            }
//        } catch (SQLException e) {
//            logger.error("Ошибка при удалении сущности", e);
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public Optional<T> findById(PK id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, (Integer) id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                T entity = mapRow(resultSet);
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void update(T entity) {
        String sql = getUpdateSql();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            fillPreparesStatementForUpdate(preparedStatement, entity);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении сущности", e);
            throw new RuntimeException(e);
        }
    }
}
