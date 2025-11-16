package com.senla.task1.service;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.OrderStatus;
import com.senla.task1.service.OrderService;

import java.io.*;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AutoService {

    private OrderService orderService = OrderService.getInstance();
    private MechanicService mechanicService = MechanicService.getInstance();
    private GaragePlaceService garagePlaceService = GaragePlaceService.getInstance();


    public void createOrder(String carModel, int mechanicId, int placeNumber, double price, int hours, int minutes) {

        Mechanic mechanic = mechanicService.findMechanicById(mechanicId);
        GaragePlace garagePlace = garagePlaceService.findPlaceByNumber(placeNumber);

        if (mechanic == null) {
            System.out.println("Ошибка: указан неверный номер механика");
            return;
        }

        if (garagePlace == null) {
            System.out.println("Ошибка: указан неверный номер гаража");
        }

        if (mechanic.isBusy() || !garagePlace.isEmpty()) {
            System.out.println("Механик или место занято!");
            return;
        }

        Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
        Order order = new Order(carModel, mechanic, garagePlace, duration, price);

        orderService.addOrder(order);
    }

    public void getAvailableSlot(int year, int month, int day) {
        List<Order> orders = orderService.getOrders();
        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59);

        long availableMechanics = mechanicService.getMechanicList().stream()
                .filter(mechanic -> mechanicService.isMechanicAvailable(mechanic, orders, startDate, endDate))
                .count();

        long availableGaragePlaces = garagePlaceService.getPlaceList().stream()
                .filter(garagePlace -> garagePlaceService.isGaragePlaceAvailable(garagePlace, orders, startDate, endDate))
                .count();

        System.out.println("Количество свободных мест в сервисе на " + day + "." + month + "." + year + " - " + Math.min(availableMechanics, availableGaragePlaces));
    }

    public void importFromCSV(String filePath) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;

            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(";");

                int id = Integer.parseInt(parts[0].trim());
                String carName = parts[1].trim();

//              Если механика с айди из файла не существует, переходим к следующей строке
                if(mechanicService.findMechanicById(Integer.parseInt(parts[2].trim())) == null) {
                    System.out.println("Механика с айди " + parts[2].trim() + " не существует, заказ не может быть добавлен");
                    continue;
                }
                Mechanic mechanic = mechanicService.findMechanicById(Integer.parseInt(parts[2].trim()));

//              Если гаражного места нет, переходим к следующей строке в файле
                if (garagePlaceService.findPlaceByNumber(Integer.parseInt(parts[3])) == null) {
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

//              Для установки занятости механика и гаражного места
                if(status.equals(OrderStatus.ACCEPTED) || status.equals(OrderStatus.WAITING)) {
                    mechanic.setBusy(true);
                    garagePlace.setEmpty(false);
                }

//              Обновление записи если запись с таким id существует
                if(orderService.findOrderById(id) != null) {
                    orderService.updateOrder(id, carName, mechanic, garagePlace, status,
                            submissionDateTime, plannedCompletionDateTime, completionDateTime,
                            endDateTime, duration, price);
                } else {
                    Order order = new Order(id, carName, mechanic, garagePlace, status, submissionDateTime,
                            plannedCompletionDateTime, completionDateTime, endDateTime, duration, price);
                    orderService.addOrder(order);
                }

            }
            System.out.println("Данные успешно экспортированы из " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось найти файл");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportOrdersToCSV(String filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            bufferedWriter.write("id;carName;mechanicId;garagePlaceNumber;status;submissionDateTime;plannedCompletionDateTime;completionDateTime;endDateTime;duration;price");
            bufferedWriter.newLine();

            String lines = orderService.getOrders().stream()
                    .map(order -> String.format("%d;%s;%d;%d;%s;%s;%s;%s;%s;%d;%.2f",
                            order.getIndex(),
                            order.getCarName(),
                            order.getMechanic(),
                            order.getGaragePlace(),
                            order.getStatus(),
                            order.getSubmissionDateTime(),
                            order.getPlannedCompletionDateTime() != null ? order.getPlannedCompletionDateTime().toString() : "",
                            order.getCompletionDateTime() != null ? order.getCompletionDateTime().toString() : "",
                            order.getEndDateTime(),
                            order.getDuration(),
                            order.getPrice()
                    ))
                    .collect(Collectors.joining(System.lineSeparator()));

            bufferedWriter.write(lines);
            System.out.println("Заказы успешно экспортированы в " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
