package com.senla.task1.dao;

import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;

import java.util.Optional;

public interface OrderStatusDAO extends GenericDAO<OrderStatus, Integer> {
    Optional<OrderStatus> findByCode(OrderStatusType code);
    Boolean checkIsOrderStatusExists(Integer id);
}
