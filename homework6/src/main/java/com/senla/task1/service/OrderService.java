package com.senla.task1.service;

import com.senla.task1.dto.OrderDTO;
import com.senla.task1.dto.OrderSearchDTO;
import com.senla.task1.exceptions.OrderException;
import com.senla.task1.mapper.OrderMapper;
import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.models.enums.OrderSortType;
import com.senla.task1.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;
    private final OrderMapper orderMapper;
    private final static Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    public OrderService(@Qualifier("orderJpaDAO") OrderRepository orderRepository, OrderStatusService orderStatusService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderStatusService = orderStatusService;
        this.orderMapper = orderMapper;
    }

    public OrderRepository getOrderDAO() {
        return orderRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return findAllOrders().stream().map(orderMapper::orderToOrderDTO).collect(Collectors.toList());
    }

    @Transactional
    public void addOrder(Order order) {
        LocalDateTime endTimeLastOrder = getEndDateTimeLastOrder();
        if (endTimeLastOrder != null) {
            order.setPlannedCompletionDateTime(endTimeLastOrder);
            order.setEndDateTime(endTimeLastOrder.plus(order.getDuration()));
            orderRepository.save(order);
        } else {
            acceptOrder(order.getId());
            orderRepository.save(order);
        }
    }

    @Transactional
    public void acceptOrder(Integer id) {
        logger.info("Обработка принятия заказа № {}", id);
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderException(
                "Заказ с №" + id + " не найден"
        ));

        if (order.getStatus().getCode() == OrderStatusType.ACCEPTED) {
            return;
        }

        LocalDateTime lastEndDateTime = getEndDateTimeLastOrder();
        if (lastEndDateTime != null) {
            order.setCompletionDateTime(lastEndDateTime);
            order.setEndDateTime(lastEndDateTime.plus(order.getDuration()));
        } else {
            order.setCompletionDateTime(LocalDateTime.now());
            order.setEndDateTime(order.getCompletionDateTime().plus(order.getDuration()));
        }
        orderRepository.update(order);
        logger.info("Заказ № {} принят", id);
    }

    @Transactional
    public void deleteOrder(Integer id) {
        logger.info("Обработка удаления заказа № {}", id);
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderException(
                "Заказ с №" + id + " не найден"
        ));
        orderRepository.delete(order);
        logger.info("Заказ № {} удален", id);
    }

    @Transactional
    public void shiftOrdersTime(Integer hours, Integer minutes) {
        logger.info("Обработка сдвига времени выполнения заказов на {} часов, {} минут", hours, minutes);
        Duration time = Duration.ofHours(hours).plusMinutes(minutes);
        List<Order> orderList = findAllOrders();
        orderList.forEach(order -> order.shiftTime(time));
        orderList.forEach(order -> orderRepository.update(order));
        logger.info("Изменено время выполнения всех заказов на {} часов, {} минут", hours, minutes);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findOrderByMechanicId(Integer mechanicId) {
        logger.info("Обработка поиска заказов по механику № {}", mechanicId);
        List<OrderDTO> mechanicOrders = orderRepository.findOrderByMechanicId(mechanicId)
                .stream().map(orderMapper::orderToOrderDTO).collect(Collectors.toList());
        logger.info("Поиск заказов по механику № {} завершен", mechanicId);
        return mechanicOrders;
    }

    @Transactional(readOnly = true)
    public LocalDateTime getEndDateTimeLastOrder() {
        Order order = orderRepository.getEndDateTimeLastActiveOrder().orElse(null);

        if (order == null) {
            return null;
        }

        return order.getEndDateTime();
    }


    @Transactional(readOnly = true)
    public List<OrderDTO> findOrderByStatus(String status) {
        logger.info("Обработка вывода заказов по статусу {}", status);
        OrderStatus orderStatus = orderStatusService.findByCodeString(status);
        List<OrderDTO> ordersByStatus = orderRepository.findOrderByStatus(orderStatus)
                .stream().map(orderMapper::orderToOrderDTO).collect(Collectors.toList());
        logger.info("Поиск заказов по статусу {} завершен", status);
        return ordersByStatus;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> sortOrdersByDateOfSubmission(Boolean flag) {
        logger.info("Обработка сортировки заказов по дате подачи");
        List<OrderDTO> orderList = orderRepository.sortBy(OrderSortType.DATE_OF_SUBMISSION.getDisplayName(), flag)
                .stream().map(orderMapper::orderToOrderDTO).collect(Collectors.toList());
        logger.info("Сортировка заказов по дате подачи завершена");
        return orderList;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> sortOrdersByDateOfCompletion(Boolean flag) {
        logger.info("Обработка сортировки заказов по дате выполнения");
        List<OrderDTO> orderList = orderRepository.sortBy(OrderSortType.DATE_OF_COMPLETION.getDisplayName(), flag)
                .stream().map(orderMapper::orderToOrderDTO).collect(Collectors.toList());
        ;
        logger.info("Сортировка заказов по дате выполнения завершена");
        return orderList;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> sortOrdersByPrice(Boolean flag) {
        logger.info("Обработка сортировки заказов по цене");
        List<OrderDTO> orderList = orderRepository.sortBy(OrderSortType.PRICE.getDisplayName(), flag)
                .stream().map(orderMapper::orderToOrderDTO).collect(Collectors.toList());
        ;
        logger.info("Сортировка заказов по цене завершена");
        return orderList;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findOrdersOverPeriodOfTime(Integer fromYear, Integer fromMonth, Integer fromDay,
                                                     Integer toYear, Integer toMonth, Integer toDay, String sortType, boolean flag) {
        logger.info("Обработка поиска заказов за период времени");
        LocalDateTime startTime = LocalDateTime.of(fromYear, fromMonth, fromDay, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(toYear, toMonth, toDay, 23, 59);
        List<OrderDTO> sortedOrdersOverPeriod = orderRepository.findOrderOverPeriodOfTime(startTime, endTime, sortType, flag)
                .stream().map(orderMapper::orderToOrderDTO).collect(Collectors.toList());
        logger.info("Поиск заказов за период времени завершен");
        return sortedOrdersOverPeriod;
    }

    public LocalDateTime showNearestAvailableDate() {
        logger.info("Обработка поиска ближайшей свободной даты");
        LocalDateTime nearestAvailableSlot = getEndDateTimeLastOrder();
        logger.info("Поиск ближайшей свободной даты завершен");
        return nearestAvailableSlot;
    }

    @Transactional(readOnly = true)
    public OrderDTO findOrderById(Integer id) {
        logger.info("Поиск заказа по id {}", id);
        return orderMapper.orderToOrderDTO(findOrderByIdIsExists(id).orElseThrow(() -> new OrderException(
                "Заказ с id - " + id + " не найден"
        )));
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrderByIdIsExists(Integer id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean isOrdersExists(Integer id) {
        return orderRepository.checkIsOrderExists(id);
    }

    @Transactional
    public void update(Order order) {
        orderRepository.update(order);
    }

    @Transactional
    public List<OrderDTO> searchOrders(OrderSearchDTO orderSearchDTO) {
        if (orderSearchDTO.mechanicId() != null) {
            return findOrderByMechanicId(orderSearchDTO.mechanicId());
        } else if (orderSearchDTO.status() != null) {
            return findOrderByStatus(orderSearchDTO.status());
        } else if (orderSearchDTO.submissionDateTime() != null) {
            return sortOrdersByDateOfSubmission(orderSearchDTO.flag());
        } else if (orderSearchDTO.completionDateTime() != null) {
            return sortOrdersByDateOfCompletion(orderSearchDTO.flag());
        } else if (orderSearchDTO.price() != null) {
            return sortOrdersByPrice(orderSearchDTO.flag());
        }
        return findOrdersOverPeriodOfTime(orderSearchDTO.fromYear(), orderSearchDTO.fromMonth(), orderSearchDTO.fromDay(),
                orderSearchDTO.toYear(), orderSearchDTO.toMonth(), orderSearchDTO.toDay(), orderSearchDTO.sortType(), orderSearchDTO.flag());
    }
}