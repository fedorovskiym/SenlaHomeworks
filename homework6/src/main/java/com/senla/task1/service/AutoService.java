package com.senla.task1.service;

import com.senla.task1.dto.AutoServiceRequestDTO;
import com.senla.task1.dto.OrderDTO;
import com.senla.task1.exceptions.OrderException;
import com.senla.task1.mapper.OrderMapper;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutoService {

    private final OrderService orderService;
    private final MechanicService mechanicService;
    private final GaragePlaceService garagePlaceService;
    private final OrderStatusService orderStatusService;
    private final OrderMapper orderMapper;
    private final static Logger logger = LogManager.getLogger(AutoService.class);

    @Autowired
    public AutoService(OrderService orderService, MechanicService mechanicService, GaragePlaceService garagePlaceService, OrderStatusService orderStatusService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.mechanicService = mechanicService;
        this.garagePlaceService = garagePlaceService;
        this.orderStatusService = orderStatusService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDTO createOrder(AutoServiceRequestDTO autoServiceRequestDTO) {
        logger.info("Обработка создания нового заказа");
        Mechanic mechanic = mechanicService.findMechanicById(autoServiceRequestDTO.mechanicId());
        GaragePlace garagePlace = garagePlaceService.findPlaceByNumber(autoServiceRequestDTO.placeNumber());

        if (mechanic.getIsBusy() || !garagePlace.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mechanic or garage place is busy!");
        }

        Duration duration = Duration.ofHours(autoServiceRequestDTO.hours()).plusMinutes(autoServiceRequestDTO.minutes());
        OrderStatus orderStatus = orderStatusService.findByCode(OrderStatusType.WAITING);
        System.out.println(orderStatus);
        Order order = new Order(autoServiceRequestDTO.carModel(), mechanic, garagePlace, duration, autoServiceRequestDTO.price(), orderStatus);
        mechanic.setBusy(true);
        garagePlace.setEmpty(false);
        mechanicService.updateMechanic(mechanic);
        garagePlaceService.updateGaragePlace(garagePlace);
        orderService.addOrder(order);
        logger.info("Заказ {} создан", order);
        return orderMapper.orderToOrderDTO(order);
    }

    @Transactional(readOnly = true)
    public Integer getAvailableSlot(Integer year, Integer month, Integer day) {
        logger.info("Обработка расчета количества свободных мест на дату {}, {}, {}", year, month, day);
        List<Order> orders = orderService.findAllOrders();
        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59);

        long availableMechanics = mechanicService.findAllMechanic().stream()
                .filter(mechanic -> mechanicService.isMechanicAvailable(mechanic, orders, startDate, endDate))
                .count();

        long availableGaragePlaces = garagePlaceService.findAllGaragePlace().stream()
                .filter(garagePlace -> garagePlaceService.isGaragePlaceAvailable(garagePlace, orders, startDate, endDate))
                .count();

        logger.info("Получения расчета свободных мест на дату {}, {}, {} завершена", year, month, day);
        return (int) Math.min(availableMechanics, availableGaragePlaces);
    }

    @Transactional
    public void importFromCSV(String resourceName) {
        logger.info("Обработка импорта заказов из файла {}", resourceName);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("csv/".concat(resourceName))) {

            if (inputStream == null) {
                throw new FileNotFoundException("Resource with name " + resourceName + " not found");
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                boolean firstLine = true;

                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                            continue;
                        }

                        String[] parts = line.split(";");

                        int id = Integer.parseInt(parts[0].trim());
                        String carName = parts[1].trim();

                        if (!mechanicService.isMechanicExists(Integer.parseInt(parts[2].trim()))) {
                            continue;
                        }
                        Mechanic mechanic = mechanicService.findMechanicById(Integer.parseInt(parts[2].trim()));

                        if (!garagePlaceService.isGaragePlaceExists(Integer.parseInt(parts[3].trim()))) {
                            continue;
                        }
                        GaragePlace garagePlace = garagePlaceService.findPlaceByNumber(Integer.parseInt(parts[3].trim()));

                        if (!orderStatusService.checkIsOrderStatusExists(Integer.parseInt(parts[4].trim()))) {
                            continue;
                        }
                        OrderStatus orderStatus = orderStatusService.findById(Integer.parseInt(parts[4].trim()));

                        LocalDateTime submissionDateTime = LocalDateTime.parse(parts[5].trim());
                        LocalDateTime plannedCompletionDateTime = parts[6].trim().isEmpty() ? null :
                                LocalDateTime.parse(parts[6].trim());
                        LocalDateTime completionDateTime = parts[7].trim().isEmpty() ? null :
                                LocalDateTime.parse(parts[7].trim());
                        LocalDateTime endDateTime = parts[8].trim().isEmpty() ? null :
                                LocalDateTime.parse(parts[8].trim());
                        Duration duration = Duration.ofMinutes(Long.parseLong(parts[9].trim()));
                        double price = Double.parseDouble(parts[10].trim().replace(',', '.'));

                        if (orderStatus.getCode().equals(OrderStatusType.ACCEPTED) || orderStatus.getCode().equals(OrderStatusType.WAITING)) {
                            mechanic.setBusy(true);
                            garagePlace.setEmpty(false);
                        }

                        if (orderService.isOrdersExists(id)) {
                            updateOrder(id, carName, mechanic, garagePlace, orderStatus,
                                    submissionDateTime, plannedCompletionDateTime, completionDateTime,
                                    endDateTime, duration, price);
                        } else {
                            Order order = new Order(id, carName, mechanic, garagePlace, orderStatus, submissionDateTime,
                                    plannedCompletionDateTime, completionDateTime, endDateTime, duration, price);
                            orderService.addOrder(order);
                        }

                        mechanicService.updateMechanic(mechanic);
                        garagePlaceService.updateGaragePlace(garagePlace);
                    }

                    logger.info("Данные импортированы из файла {}", resourceName);
                } catch (Exception e) {
                    logger.info("Ошибка при импорте данных из файла {}", resourceName, e);
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String exportOrdersToCSV() {
        logger.info("Обработка экспорта данных заказов в файл");
        List<Order> orders = orderService.findAllOrders();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id;carName;mechanicId;garagePlaceNumber;status;submissionDateTime;plannedCompletionDateTime;" +
                "completionDateTime;endDateTime;Duration;price").append(System.lineSeparator());
        orders.forEach(order -> stringBuilder.append(String.format("%d;%s;%d;%d;%s;%s;%s;%s;%s;%d;%.2f",
                        order.getId(),
                        order.getCarName(),
                        order.getMechanic().getId(),
                        order.getGaragePlace().getId(),
                        order.getStatus(),
                        order.getSubmissionDateTime(),
                        order.getPlannedCompletionDateTime() != null ? order.getPlannedCompletionDateTime().toString() : "",
                        order.getCompletionDateTime() != null ? order.getCompletionDateTime().toString() : "",
                        order.getEndDateTime(),
                        order.getDuration().toMinutes(),
                        order.getPrice()))
                .append(System.lineSeparator()));
        logger.info("Экспорт данных в файл завершен");
        return stringBuilder.toString();
    }

    @Transactional
    public void updateOrder(Integer id, String carName, Mechanic mechanic,
                            GaragePlace garagePlace, OrderStatus orderStatus, LocalDateTime submissionDateTime,
                            LocalDateTime plannedCompletionDateTime, LocalDateTime completionDateTime,
                            LocalDateTime endDateTime, Duration duration, Double price) {

        Order order = orderService.findOrderByIdIsExists(id).orElse(null);

//      Если для существующего заказа из файла считывается другой механик, то старому механику меняется статус на свободный, а новому на занятый
        if (order.getMechanic().getId() != mechanic.getId()) {
            order.getMechanic().setBusy(false);
            mechanic.setBusy(true);
        }

//      Аналогично с механиками
        if (order.getGaragePlace().getPlaceNumber() != garagePlace.getPlaceNumber()) {
            order.getGaragePlace().setEmpty(true);
            garagePlace.setEmpty(false);
        }

        order.setCarName(carName);
        order.setMechanic(mechanic);
        order.setGaragePlace(garagePlace);
        order.setStatus(orderStatus);
        order.setSubmissionDateTime(submissionDateTime);
        order.setPlannedCompletionDateTime(plannedCompletionDateTime);
        order.setCompletionDateTime(completionDateTime);
        order.setEndDateTime(endDateTime);
        order.setDuration(duration);
        order.setPrice(price);
        orderService.update(order);
    }

    @Transactional
    public void deleteOrder(Integer id) {
        logger.info("Обработка удаления заказа № {}", id);
        orderService.deleteOrder(id);
        logger.info("Заказ № {} удален", id);
    }

    @Transactional
    public void closeOrder(Integer id) {
        logger.info("Обработка закрытия заказа № {}", id);
        Order order = orderService.getOrderDAO().findById(id).orElseThrow(() -> new OrderException(
                "Заказ с №" + id + " не найден"
        ));

        order.setStatus(orderStatusService.findByCode(OrderStatusType.DONE));
        orderService.update(order);
        logger.info("Заказ № {} закрыт", id);
        orderService.acceptOrder(id + 1);
    }

    @Transactional
    public void cancelOrder(Integer id) {
        logger.info("Обработка отмены заказа № {}", id);
        Order order = orderService.getOrderDAO().findById(id).orElseThrow(() -> new OrderException(
                "Заказ с №" + id + " не найден"
        ));
        order.setStatus(orderStatusService.findByCode(OrderStatusType.CANCEL));
        order.cancelOrder();
        orderService.update(order);
        logger.info("Заказ № {} отменен", id);
    }
}
