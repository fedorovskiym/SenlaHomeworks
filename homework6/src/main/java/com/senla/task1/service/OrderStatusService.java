package com.senla.task1.service;

import com.senla.task1.exceptions.OrderStatusException;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;

    @Autowired
    public OrderStatusService(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    public OrderStatus findByCode(OrderStatusType code) {
        return orderStatusRepository.findByCode(code).orElseThrow(() -> new OrderStatusException(
                "Статуса заказа с кодом " + code + " нет"
        ));
    }

    public OrderStatus findById(Integer id) {
        return orderStatusRepository.findById(id).orElseThrow(() -> new OrderStatusException(
                "Статуса заказа с id " + id + " нет"
        ));
    }

    public Boolean checkIsOrderStatusExists(Integer id) {
        return orderStatusRepository.checkIsOrderStatusExists(id);
    }
}
