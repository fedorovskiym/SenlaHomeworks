package com.senla.task1.dao;

import com.senla.task1.models.Mechanic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MechanicDAOImpl extends GenericDAOImpl<Mechanic, Integer> implements MechanicDAO {

    private static final String SQL_ORDER_BY = "SELECT * FROM mechanic ORDER BY ";
    private static final Logger logger = LogManager.getLogger(MechanicDAOImpl.class);

    @Override
    protected String getTableName() {
        return "mechanic";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO " + getTableName() + " (name, surname, experience_years, is_busy) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE " + getTableName() + " SET name=?, surname=?, experience_years=?, is_busy=? WHERE id=?";
    }

    @Override
    protected void fillPreparesStatementForInsert(PreparedStatement preparedStatement, Mechanic mechanic) throws SQLException {
        preparedStatement.setString(1, mechanic.getName());
        preparedStatement.setString(2, mechanic.getSurname());
        preparedStatement.setDouble(3, mechanic.getExperience());
        preparedStatement.setBoolean(4, mechanic.isBusy());
    }

    @Override
    protected void fillPreparesStatementForUpdate(PreparedStatement preparedStatement, Mechanic mechanic) throws SQLException {
        preparedStatement.setString(1, mechanic.getName());
        preparedStatement.setString(2, mechanic.getSurname());
        preparedStatement.setDouble(3, mechanic.getExperience());
        preparedStatement.setBoolean(4, mechanic.isBusy());
        preparedStatement.setInt(5, mechanic.getId());
    }

    @Override
    protected Mechanic mapRow(ResultSet resultSet) throws SQLException {
        return new Mechanic(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("surname"),
                resultSet.getDouble("experience_years"), resultSet.getBoolean("is_busy"));
    }

    @Override
    public Optional<Mechanic> findById(Integer id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Mechanic mechanic = mapRow(resultSet);
                    return Optional.of(mechanic);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Ошибка при нахождении механика № {}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Mechanic> sortBy(String field, boolean flag) {
        List<Mechanic> mechanicList = new ArrayList<>();
        String sql;
        if (flag) {
            sql = SQL_ORDER_BY.concat(field).concat(" DESC");
        } else {
            sql = SQL_ORDER_BY.concat(field).concat(" ASC");
        }
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Mechanic mechanic = mapRow(resultSet);
                mechanicList.add(mechanic);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сортировке механиков по {}", field, e);
            throw new RuntimeException(e);
        }
        return mechanicList;
    }

    @Override
    public Boolean checkIsMechanicExists(Integer id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при проверки существования механика № {}", id, e);
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void importWithTransaction(List<Mechanic> mechanicList) {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            for (Mechanic mechanic : mechanicList) {
                if (checkIsMechanicExists(mechanic.getId())) {
                    update(mechanic);
                } else {
                    save(mechanic);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
