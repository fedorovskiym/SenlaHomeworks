package com.senla.task1.service;

import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.exceptions.OrderException;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.OrderStatus;
import com.senla.task1.models.enums.SortType;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class OrderService {

    private final List<Order> orders = new ArrayList<>();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final String folderPath = "data";
    private final String fileName = "order.bin";

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис заказов создался");
    }

    public OrderService() {
        load();
        registerShutdown();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        if (getEndDateTimeLastOrder() != null) {
            order.setPlannedCompletionDateTime(getEndDateTimeLastOrder());
            order.setEndDateTime(getEndDateTimeLastOrder().plus(order.getDuration()));
            orders.add(order);
        } else {
            orders.add(order);
            acceptOrder(order.getIndex());
        }
        System.out.println("Заказ: " + order.getCarName() + " принят в гараж №" + order.getGaragePlace().getPlaceNumber() + ". Назначен механик " + order.getMechanic().getName());
        System.out.println("Статус заказа: '" + order.getStatus().getDisplayName() + "'");
        if (order.getPlannedCompletionDateTime() != null) {
            System.out.println("Примерное начало выполнения заказа: " + order.getPlannedCompletionDateTime().format(dateTimeFormatter));
        }
        System.out.println();
    }

    public void acceptOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                if (order.getStatus().equals(OrderStatus.ACCEPTED)) {
                    System.out.println("Заказ №" + id + " уже принят\n");
                    return;
                }
                order.acceptOrder();
                // если перед заказом был другой, и у него есть время окончания, то новому заказу датой выполнения ставится дата окончания предыдущего заказа, если такого нет, то время выполнения ставится 'прямо сейчас'
                if (getEndDateTimeLastOrder() != null) {
                    order.setCompletionDateTime(getEndDateTimeLastOrder());
                    order.setEndDateTime(getEndDateTimeLastOrder().plus(order.getDuration()));
                } else {
                    order.setCompletionDateTime(LocalDateTime.now());
                    order.setEndDateTime(LocalDateTime.now().plus(order.getDuration()));
                }
                System.out.println("Заказ №" + id + " принят и выполняется\n");
                return;
            }
        }
        throw new OrderException("Заказ №" + id + " не найден");
    }

    public void closeOrder(int id) {
        Order order = orders.stream().filter(order1 -> order1.getIndex() == id)
                .findFirst()
                .orElseThrow(() -> new OrderException("Заказ №" + id + " не найден"));

        order.closeOrder();
        System.out.println("Заказ №" + id + " закрыт\n");
        acceptOrder(id + 1);
    }

    public void cancelOrder(int id) {
        Order order = orders.stream().filter(order1 -> order1.getIndex() == id)
                .findFirst()
                .orElseThrow(() -> new OrderException("Заказ №" + id + " не найден"));

        order.cancelOrder();
        System.out.println("Заказ №" + id + " закрыт\n");
    }

    public void shiftOrdersTime(int hours, int minutes) {
        Duration time = Duration.ofHours(hours).plusMinutes(minutes);
        orders.forEach(order -> order.getDuration().plus(time));
        System.out.println("Изменено время выполнения всех заказов на " + time.toHours() + " час(а/ов) " + time.toMinutesPart() + " минут\n");
    }

    public void deleteOrder(int id) {
        Order order = orders.stream().filter(order1 -> order1.getIndex() == id)
                .findFirst()
                .orElseThrow(() -> new OrderException("Заказ №" + id + " не найден"));

        orders.remove(order);
        order.deleteOrder();
        System.out.println("Заказ №" + id + " удален\n");
    }

    // Вывод заказа по айдишнку механика
    public void findOrderByMechanicId(int mechanicId) {
        List<Order> mechanicOrders = orders.stream().filter(order -> order.getMechanic().getIndex() == mechanicId).toList();

        if (!mechanicOrders.isEmpty()) {
            mechanicOrders.forEach(order -> System.out.println(formatOrderInfo(order)));
        } else {
            System.out.println("У данного мастера в данный момент нет заказов");
        }

    }

    // Получение времени окончания последнего активного заказа
    public LocalDateTime getEndDateTimeLastOrder() {
        if (getLastActiveOrder() == null) {
            return null;
        }
        return getLastActiveOrder().getEndDateTime();
    }

    // Получение последнего заказа со статусом 'Ожидает' или 'Принят'
    public Order getLastActiveOrder() {
        for (int i = orders.size() - 1; i >= 0; i--) {
            Order order = orders.get(i);
            if (!order.getStatus().equals(OrderStatus.CANCEL) && !order.getStatus().equals(OrderStatus.DELETED) && !order.getStatus().equals(OrderStatus.DONE)) {
                return order;
            }
        }
        return null;
    }

    // Вывод заказов по статусу
    public void showOrders(String status) {
        List<Order> ordersByStatus = orders.stream().filter(order -> order.getStatus().getDisplayName().equals(status)).toList();

        if (!ordersByStatus.isEmpty()) {
            ordersByStatus.forEach(order -> System.out.println(formatOrderInfo(order)));
        } else {
            System.out.println("Заказов с таким статусом нет");
        }
    }

    // Вывод всех заказов
    public void showOrders() {
        System.out.println("Заказы:");
        orders.forEach(order -> System.out.println(formatOrderInfo(order)));
    }

    // Сортировка по дате подачи заявки (flag - определяет отображение)
    public void sortOrdersByDateOfSubmission(boolean flag) {
        Comparator<Order> comparator = Comparator.comparing(Order::getSubmissionDateTime);

        if (!flag) {
            comparator = comparator.reversed();
        }

        orders.sort(comparator);
    }

    // Сортировка по дате выполнения (flag - определяет отображение), если у заказа нет даты выполнения, а только планируемая, то они становятся в конец
    public void sortOrdersByDateOfCompletion(boolean flag) {
        Comparator<Order> comparator = Comparator.comparing(Order::getCompletionDateTime, Comparator.nullsLast(Comparator.naturalOrder()));

        if (!flag) {
            comparator = comparator.reversed();
        }

        orders.sort(comparator);
    }

    // Сортировка по цене (flag - определяет отображение)
    public void sortOrdersByPrice(boolean flag) {
        Comparator<Order> comparator = Comparator.comparing(Order::getPrice);

        if (!flag) {
            comparator = comparator.reversed();
        }

        orders.sort(comparator);
    }

    // Метод для определения каким способом сортировать и выводить заявки за период времени
    public void findOrdersOverPeriodOfTime(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay, SortType sortType, boolean flag) {
        LocalDateTime startTime = LocalDateTime.of(fromYear, fromMonth, fromDay, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(toYear, toMonth, toDay, 23, 59);
        System.out.println("Заказы в период с " + startTime.format(dateTimeFormatter) + " по " + endTime.format(dateTimeFormatter));
        System.out.println();
        switch (sortType) {
            case DATE_OF_SUBMISSION: {
                sortOrdersByDateOfSubmission(flag);
                break;
            }
            case DATE_OF_COMPLETION: {
                sortOrdersByDateOfCompletion(flag);
                break;
            }
            case PRICE: {
                sortOrdersByPrice(flag);
                break;
            }
        }
        showOrdersOverPeriodOfTime(startTime, endTime);
        sortOrdersByDateOfSubmission(true);
    }

    // Вывод заказов за период времени по дате подачи заявки
    public void showOrdersOverPeriodOfTime(LocalDateTime startDate, LocalDateTime endDate) {
        System.out.println("Заказы:");
        orders.stream()
                .filter(order -> !order.getStatus().equals(OrderStatus.WAITING) && !order.getStatus().equals(OrderStatus.DONE))
                .filter(order -> order.getSubmissionDateTime().isAfter(startDate) && order.getSubmissionDateTime().isBefore(endDate))
                .forEach(order -> System.out.println(formatOrderInfo(order)));
    }

    public void showNearestAvailableDate() {
        System.out.println("Ближайшая свободная дата: " + getEndDateTimeLastOrder().format(dateTimeFormatter));
    }

    private String formatOrderInfo(Order order) {
        return String.format(
                """
                        Заказ №%d:
                        Статус: %s
                        Механик: %s %s
                        Название машины: %s
                        Место в гараже: %d
                        Дата подачи заявки: %s
                        %s\
                        %s\
                        %s\
                        %s\
                        Цена: %.2f руб. \n
                        """,
                order.getIndex(),
                order.getStatus().getDisplayName(),
                order.getMechanic().getName(),
                order.getMechanic().getSurname(),
                order.getCarName(),
                order.getGaragePlace().getPlaceNumber(),
                order.getSubmissionDateTime().format(dateTimeFormatter),

                order.getStatus().equals(OrderStatus.WAITING)
                        ? String.format("Планируемая дата выполнения заказа: %s\n",
                        order.getPlannedCompletionDateTime().format(dateTimeFormatter))
                        : "",

                order.getDuration() != null
                        ? String.format("Длительность: %d ч. %d мин.\n",
                        order.getDuration().toHours(), order.getDuration().toMinutesPart())
                        : "",

                order.getCompletionDateTime() != null
                        ? String.format("Начало выполнения заказа: %s\n",
                        order.getCompletionDateTime().format(dateTimeFormatter))
                        : "",

                order.getEndDateTime() != null
                        ? String.format("Конец выполнения: %s\n",
                        order.getEndDateTime().format(dateTimeFormatter))
                        : "",

                order.getPrice()
        );
    }

    public Order findOrderById(int id) {
        return orders.stream().filter(order -> order.getIndex() == id).findFirst()
                .orElseThrow(() -> new OrderException("Заказ №" + id + " не найден"));
    }

    public void updateOrder(int id, String carName, Mechanic mechanic,
                            GaragePlace garagePlace, OrderStatus status, LocalDateTime submissionDateTime,
                            LocalDateTime plannedCompletionDateTime, LocalDateTime completionDateTime,
                            LocalDateTime endDateTime, Duration duration, double price) {

        Order order = findOrderById(id);

//      Если для существующего заказа из файла считывается другой механик, то старому механику меняетс статус на свободный, а новому на занятый
        if (order.getMechanic().getIndex() != mechanic.getIndex()) {
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

        System.out.println("Заказ №" + id + " обновлен");
    }

    public boolean isOrdersExists(int id) {
        return orders.stream().anyMatch(order -> order.getIndex() == id);
    }

    public void save() {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(orders);
                System.out.println("Состояние заказов сохранено");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сериализации файла");
        }
    }

    private void load() {
        File file = new File(folderPath, fileName);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Order> loadedList = (List<Order>) ois.readObject();
            orders.clear();
            orders.addAll(loadedList);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при десериализации файла");
        }
    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

}


