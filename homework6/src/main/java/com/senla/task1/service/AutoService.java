package com.senla.task1.service;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.exceptions.MechanicException;
import com.senla.task1.exceptions.OrderException;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.OrderStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AutoService {

    private final OrderService orderService;
    private final MechanicService mechanicService;
    private final GaragePlaceService garagePlaceService;

    @Inject
    public AutoService(OrderService orderService, MechanicService mechanicService, GaragePlaceService garagePlaceService) {
        this.orderService = orderService;
        this.mechanicService = mechanicService;
        this.garagePlaceService = garagePlaceService;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Общий сервис создался");
    }

    public void createOrder(String carModel, Integer mechanicId, Integer placeNumber, Double price, Integer hours, Integer minutes) {
        Mechanic mechanic = mechanicService.findMechanicById(mechanicId);
        GaragePlace garagePlace = garagePlaceService.findPlaceByNumber(placeNumber);

        if (mechanic.isBusy() || !garagePlace.isEmpty()) {
            System.out.println("Механик или место занято!");
            return;
        }

        Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
        Order order = new Order(carModel, mechanic, garagePlace, duration, price);

        Connection conn = null;
        try {
            conn = orderService.getOrderDAO().getConnection();
            conn.setAutoCommit(false);

            mechanic.setBusy(true);
            garagePlace.setEmpty(false);
            mechanicService.updateMechanic(mechanic);
            garagePlaceService.updateGaragePlace(garagePlace);

            orderService.addOrder(order);

            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Ошибка при создании заказа", e);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getAvailableSlot(Integer year, Integer month, Integer day) {
        List<Order> orders = orderService.findAllOrders();
        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59);

        long availableMechanics = mechanicService.findAllMechanic().stream()
                .filter(mechanic -> mechanicService.isMechanicAvailable(mechanic, orders, startDate, endDate))
                .count();

        long availableGaragePlaces = garagePlaceService.findAllGaragePlace().stream()
                .filter(garagePlace -> garagePlaceService.isGaragePlaceAvailable(garagePlace, orders, startDate, endDate))
                .count();

        System.out.println("Количество свободных мест в сервисе на " + day + "." + month + "." + year + " - " + Math.min(availableMechanics, availableGaragePlaces));
    }

    public void importFromCSV(String resourceName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("csv/".concat(resourceName))) {

            if (inputStream == null) {
                throw new FileNotFoundException("Ресурс не найден: " + resourceName);
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                boolean firstLine = true;

                Connection connection = orderService.getOrderDAO().getConnection();
                connection.setAutoCommit(false);

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
                            System.out.println("Механика с айди " + parts[2].trim() + " не существует, заказ не может быть добавлен");
                            continue;
                        }
                        Mechanic mechanic = mechanicService.findMechanicById(Integer.parseInt(parts[2].trim()));

                        if (!garagePlaceService.isGaragePlaceExists(Integer.parseInt(parts[3].trim()))) {
                            System.out.println("Места в гараже № " + Integer.parseInt(parts[3]) + " не существует, заказ не может быть добавлен");
                            continue;
                        }
                        GaragePlace garagePlace = garagePlaceService.findPlaceByNumber(Integer.parseInt(parts[3].trim()));

                        OrderStatus status = OrderStatus.valueOf(parts[4].trim());
                        LocalDateTime submissionDateTime = LocalDateTime.parse(parts[5].trim());
                        LocalDateTime plannedCompletionDateTime = parts[6].trim().isEmpty() ? null :
                                LocalDateTime.parse(parts[6].trim());
                        LocalDateTime completionDateTime = parts[7].trim().isEmpty() ? null :
                                LocalDateTime.parse(parts[7].trim());
                        LocalDateTime endDateTime = parts[8].trim().isEmpty() ? null :
                                LocalDateTime.parse(parts[8].trim());
                        Duration duration = Duration.ofMinutes(Long.parseLong(parts[9].trim()));
                        double price = Double.parseDouble(parts[10].trim().replace(',', '.'));

                        if (status.equals(OrderStatus.ACCEPTED) || status.equals(OrderStatus.WAITING)) {
                            mechanic.setBusy(true);
                            garagePlace.setEmpty(false);
                        }

                        if (orderService.isOrdersExists(id)) {
                            updateOrder(id, carName, mechanic, garagePlace, status,
                                    submissionDateTime, plannedCompletionDateTime, completionDateTime,
                                    endDateTime, duration, price);
                        } else {
                            if (mechanic.isBusy() || !garagePlace.isEmpty()) {
                                System.out.println("Механик или место занято, заказ не может быть добавлен");
                                continue;
                            }
                            Order order = new Order(id, carName, mechanic, garagePlace, status, submissionDateTime,
                                    plannedCompletionDateTime, completionDateTime, endDateTime, duration, price);
                            orderService.addOrder(order);
                        }

                        mechanicService.updateMechanic(mechanic);
                        garagePlaceService.updateGaragePlace(garagePlace);
                    }

                    connection.commit();
                    System.out.println("Данные успешно импортированы из " + resourceName);

                } catch (Exception e) {
                    connection.rollback();
                    throw new RuntimeException("Ошибка при импорте заказов", e);
                } finally {
                    connection.setAutoCommit(true);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportOrdersToCSV(String filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            bufferedWriter.write("id;carName;mechanicId;garagePlaceNumber;status;submissionDateTime;plannedCompletionDateTime;completionDateTime;endDateTime;duration;price");
            bufferedWriter.newLine();

            String lines = orderService.findAllOrders().stream()
                    .map(order -> String.format("%d;%s;%d;%d;%s;%s;%s;%s;%s;%d;%.2f",
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
                            order.getPrice()
                    ))
                    .collect(Collectors.joining(System.lineSeparator()));

            bufferedWriter.write(lines);
            System.out.println("Заказы успешно экспортированы в " + filePath);
        } catch (IOException e) {
            throw new OrderException("Ошибка при экспорте данных");
        }
    }

    public void updateOrder(Integer id, String carName, Mechanic mechanic,
                            GaragePlace garagePlace, OrderStatus status, LocalDateTime submissionDateTime,
                            LocalDateTime plannedCompletionDateTime, LocalDateTime completionDateTime,
                            LocalDateTime endDateTime, Duration duration, Double price) {

        Order order = orderService.findOrderById(id);

//      Если для существующего заказа из файла считывается другой механик, то старому механику меняетс статус на свободный, а новому на занятый
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
        order.setStatus(status);
        order.setSubmissionDateTime(submissionDateTime);
        order.setPlannedCompletionDateTime(plannedCompletionDateTime);
        order.setCompletionDateTime(completionDateTime);
        order.setEndDateTime(endDateTime);
        order.setDuration(duration);
        order.setPrice(price);
        orderService.update(order);
        System.out.println("Заказ №" + id + " обновлен");
    }

}
