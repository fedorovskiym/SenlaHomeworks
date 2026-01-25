package com.senla.task1.dao;

import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Order;

import javax.print.attribute.standard.MediaSize;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GaragePlaceDAOImpl extends GenericDAOImpl<GaragePlace, Integer> implements GaragePlaceDAO {

    @PostConstruct
    public void post() {
        System.out.println("GaragePlaceDAOImpl создался");
    }

    @Override
    public String getTableName() {
        return "garage_place";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO " + getTableName() + "(place_number, is_empty) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE " + getTableName() + " SET place_number=?, is_empty=? WHERE id=?";
    }

    @Override
    protected void fillPreparesStatementForInsert(PreparedStatement preparedStatement, GaragePlace garagePlace) throws SQLException {
        preparedStatement.setInt(1, garagePlace.getPlaceNumber());
        preparedStatement.setBoolean(2, garagePlace.isEmpty());
    }

    @Override
    protected void fillPreparesStatementForUpdate(PreparedStatement preparedStatement, GaragePlace garagePlace) throws SQLException {
        preparedStatement.setInt(1, garagePlace.getPlaceNumber());
        preparedStatement.setBoolean(2, garagePlace.isEmpty());
        preparedStatement.setInt(3, garagePlace.getId());
    }

    @Override
    protected GaragePlace mapRow(ResultSet resultSet) throws SQLException {
        return new GaragePlace(resultSet.getInt("id"),
                resultSet.getInt("place_number"), resultSet.getBoolean("is_empty"));
    }

    @Override
    public Boolean checkIsPlaceNumberExists(int placeNumber) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE place_number=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, placeNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public List<GaragePlace> findFreeGaragePlaces() {
        List<GaragePlace> freeGaragePlaces = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " WHERE is_empty = true";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                GaragePlace garagePlace = mapRow(resultSet);
                freeGaragePlaces.add(garagePlace);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return freeGaragePlaces;
    }

    @Override
    public Optional<GaragePlace> findByPlaceNumber(int placeNumber) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE place_number=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, placeNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    GaragePlace garagePlace = mapRow(resultSet);
                    return Optional.of(garagePlace);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<GaragePlace> findById(Integer id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                GaragePlace garagePlace = mapRow(resultSet);
                return Optional.of(garagePlace);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void importWithTransaction(List<GaragePlace> garagePlaces) {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            for (GaragePlace garagePlace : garagePlaces) {
                if (checkIsPlaceNumberExists(garagePlace.getPlaceNumber())) {
                    update(garagePlace);
                } else {
                    save(garagePlace);
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
