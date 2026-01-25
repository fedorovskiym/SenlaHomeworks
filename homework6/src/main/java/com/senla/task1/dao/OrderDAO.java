package com.senla.task1.dao;

import com.senla.task1.models.Order;
import com.senla.task1.models.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDAO extends GenericDAO<Order, Integer> {
    Optional<Order> getEndDateTimeLastActiveOrder();

    List<Order> sortBy(String field, boolean flag);

    List<Order> findOrderByMechanicId(Integer mechanicId);

    List<Order> findOrderByStatus(OrderStatus status);

    Boolean checkIsOrderExists(Integer id);

    List<Order> findOrderOverPeriodOfTime(LocalDateTime start, LocalDateTime end, String field, boolean flag);
}
