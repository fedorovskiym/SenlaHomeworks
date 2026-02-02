package com.senla.task1.service;

import com.senla.task1.dao.impl.jpa.OrderStatusJpaDAOImpl;
import com.senla.task1.exceptions.OrderStatusException;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {

    private final OrderStatusJpaDAOImpl orderStatusJpaDAO;

    @Autowired
    public OrderStatusService(OrderStatusJpaDAOImpl orderStatusJpaDAO) {
        this.orderStatusJpaDAO = orderStatusJpaDAO;
    }

    public OrderStatusJpaDAOImpl getOrderStatusJpaDAO() {
        return orderStatusJpaDAO;
    }

    public OrderStatus findByCode(OrderStatusType code) {
        return orderStatusJpaDAO.findByCode(code).orElseThrow(() -> new OrderStatusException(
                "Статуса заказа с кодом " + code + " нет"
        ));
    }

    public OrderStatus findById(Integer id) {
        return orderStatusJpaDAO.findById(id).orElseThrow(() -> new OrderStatusException(
                "Статуса заказа с id " + id + " нет"
        ));
    }

    public Boolean checkIsOrderStatusExists(Integer id) {
        return orderStatusJpaDAO.checkIsOrderStatusExists(id);
    }
}
