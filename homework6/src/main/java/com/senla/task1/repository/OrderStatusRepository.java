package com.senla.task1.repository;

import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;

import java.util.Optional;

public interface OrderStatusRepository extends GenericRepository<OrderStatus, Integer> {
    Optional<OrderStatus> findByCode(OrderStatusType code);
    Boolean checkIsOrderStatusExists(Integer id);
}
