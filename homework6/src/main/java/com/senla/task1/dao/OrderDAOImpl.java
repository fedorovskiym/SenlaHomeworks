package com.senla.task1.dao;

import com.senla.task1.annotations.Inject;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.OrderStatus;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl extends GenericDAOImpl<Order, Integer> implements OrderDAO {

    private final MechanicDAOImpl mechanicDAO;
    private final GaragePlaceDAOImpl garagePlaceDAO;
    private final static String SQL_ORDER_BY = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM orders ORDER BY ";


    @Inject
    public OrderDAOImpl(MechanicDAOImpl mechanicDAO, GaragePlaceDAOImpl garagePlaceDAO) {
        this.mechanicDAO = mechanicDAO;
        this.garagePlaceDAO = garagePlaceDAO;
    }

    @Override
    protected String getTableName() {
        return "orders";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO " + getTableName() + " (car_name, mechanic_id, garage_place_id, order_status, submission_date_time, " +
                "planned_completion_date_time, completion_date_time, end_date_time, duration, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? * INTERVAL '1 second', ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE " + getTableName() + " SET car_name=?, mechanic_id=?, garage_place_id=?, order_status=?, submission_date_time=?, " +
                "planned_completion_date_time=?, completion_date_time=?, end_date_time=?, duration=? * INTERVAL '1 second', price=? WHERE id=?";
    }

    @Override
    protected void fillPreparesStatementForInsert(PreparedStatement preparedStatement, Order order) throws SQLException {
        preparedStatement.setString(1, order.getCarName());
        preparedStatement.setInt(2, order.getMechanic().getId());
        preparedStatement.setInt(3, order.getGaragePlace().getId());
        preparedStatement.setString(4, String.valueOf(order.getStatus()));
        preparedStatement.setTimestamp(5, order.getSubmissionDateTime() != null ? Timestamp.valueOf(order.getSubmissionDateTime()) : null);
        preparedStatement.setTimestamp(6, order.getPlannedCompletionDateTime() != null ? Timestamp.valueOf(order.getPlannedCompletionDateTime()) : null);
        preparedStatement.setTimestamp(7, order.getCompletionDateTime() != null ? Timestamp.valueOf(order.getCompletionDateTime()) : null);
        preparedStatement.setTimestamp(8, order.getEndDateTime() != null ? Timestamp.valueOf(order.getEndDateTime()) : null);
        preparedStatement.setLong(9, order.getDuration().toSeconds());
        preparedStatement.setDouble(10, order.getPrice());
    }

    @Override
    protected void fillPreparesStatementForUpdate(PreparedStatement preparedStatement, Order order) throws SQLException {
        preparedStatement.setString(1, order.getCarName());
        preparedStatement.setInt(2, order.getMechanic().getId());
        preparedStatement.setInt(3, order.getGaragePlace().getId());
        preparedStatement.setString(4, String.valueOf(order.getStatus()));
        preparedStatement.setTimestamp(5, order.getSubmissionDateTime() != null ? Timestamp.valueOf(order.getSubmissionDateTime()) : null);
        preparedStatement.setTimestamp(6, order.getPlannedCompletionDateTime() != null ? Timestamp.valueOf(order.getPlannedCompletionDateTime()) : null);
        preparedStatement.setTimestamp(7, order.getCompletionDateTime() != null ? Timestamp.valueOf(order.getCompletionDateTime()) : null);
        preparedStatement.setTimestamp(8, order.getEndDateTime() != null ? Timestamp.valueOf(order.getEndDateTime()) : null);
        preparedStatement.setLong(9, order.getDuration().toSeconds());
        preparedStatement.setDouble(10, order.getPrice());
        preparedStatement.setInt(11, order.getId());
    }

    @Override
    protected Order mapRow(ResultSet resultSet) throws SQLException {
        Mechanic mechanic = mechanicDAO.findById(resultSet.getInt("mechanic_id")).orElse(null);

        GaragePlace garagePlace = garagePlaceDAO.findById(resultSet.getInt("garage_place_id")).orElse(null);

        Timestamp submission = resultSet.getTimestamp("submission_date_time");
        Timestamp planned = resultSet.getTimestamp("planned_completion_date_time");
        Timestamp completion = resultSet.getTimestamp("completion_date_time");
        Timestamp end = resultSet.getTimestamp("end_date_time");

        return new Order(
                resultSet.getInt("id"),
                resultSet.getString("car_name"),
                mechanic,
                garagePlace,
                OrderStatus.valueOf(resultSet.getString("order_status")),
                submission != null ? submission.toLocalDateTime() : null,
                planned != null ? planned.toLocalDateTime() : null,
                completion != null ? completion.toLocalDateTime() : null,
                end != null ? end.toLocalDateTime() : null,
                Duration.ofSeconds(resultSet.getLong("duration_seconds")),
                resultSet.getDouble("price")
        );
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM " + getTableName();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    @Override
    public Optional<Order> getEndDateTimeLastActiveOrder() {
        String sql = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM " + getTableName() +
                " WHERE order_status IN('" + OrderStatus.WAITING + "', '" + OrderStatus.ACCEPTED + "')" +
                "ORDER BY submission_date_time DESC " +
                "LIMIT 1";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order order = mapRow(resultSet);
                return Optional.of(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Order> findOrderById(Integer id) {
        String sql = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM " + getTableName() + " WHERE id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order order = mapRow(resultSet);
                return Optional.of(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Order> sortBy(String field, boolean flag) {
        List<Order> orderList = new ArrayList<>();
        String sql;
        if (flag) {
            sql = SQL_ORDER_BY.concat(field).concat(" DESC");
        } else {
            sql = SQL_ORDER_BY.concat(field).concat(" ASC");
        }
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = mapRow(resultSet);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderList;
    }

    @Override
    public List<Order> findOrderByMechanicId(Integer mechanicId) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM " + getTableName() + " WHERE mechanic_id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, mechanicId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = mapRow(resultSet);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderList;
    }

    @Override
    public List<Order> finalOrderByStatus(OrderStatus status) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM " + getTableName() +
                " WHERE order_status=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, String.valueOf(status));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = mapRow(resultSet);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderList;
    }

    @Override
    public Boolean checkIsOrderExists(Integer id) {
        String sql = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM " + getTableName() + " WHERE id=?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
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
    public List<Order> findOrderOverPeriodOfTime(LocalDateTime start, LocalDateTime end, String field, boolean flag) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT *, EXTRACT(EPOCH FROM duration) AS duration_seconds FROM " + getTableName() +
                " WHERE submission_date_time BETWEEN '" + Timestamp.valueOf(start) + "' AND '" + Timestamp.valueOf(end) + "'" +
                " ORDER BY " + field + (flag ? " ASC" : " DESC");
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = mapRow(resultSet);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderList;
    }
}
