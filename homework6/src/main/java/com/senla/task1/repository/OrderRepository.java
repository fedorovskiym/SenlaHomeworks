package com.senla.task1.repository;

import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends GenericRepository<Order, Integer> {
    Optional<Order> getEndDateTimeLastActiveOrder();

    List<Order> sortBy(String field, boolean flag);

    List<Order> findOrderByMechanicId(Integer mechanicId);

    List<Order> findOrderByStatus(OrderStatus status);

    Boolean checkIsOrderExists(Integer id);

    List<Order> findOrderOverPeriodOfTime(LocalDateTime start, LocalDateTime end, String field, boolean flag);
}
