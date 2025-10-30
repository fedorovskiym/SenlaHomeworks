package com.senla.task1.service;

import com.senla.task1.models.Order;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class OrderService {

    private List<Order> orders = new ArrayList<>();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public void addOrder(Order order) {
        if (getEndDateTimeLastOrder() != null && (orders.get(orders.size() - 1).getStatus().equals("Принят") || orders.get(orders.size() - 1).getStatus().equals("Ожидает"))) {
            order.setPlannedCompletionDateTime(getEndDateTimeLastOrder());
            order.setEndDateTime(getEndDateTimeLastOrder().plus(order.getDurationHours()));
//            setPlannedCompletionAndEndDateTime(order.getIndex(), getEndDateTimeLastOrder());
            System.out.println("Поставил");
        } else {
            order.acceptOrder();
        }
        orders.add(order);
        System.out.println("Заказ: " + order.getCarName() + " принят в гараж №" + order.getGaragePlace().getPlaceNumber() + ". Назначен механик " + order.getMechanic().getName() + ". Назначен статус 'Ожидает'.");
        if (order.getPlannedCompletionDateTime() != null) {
            System.out.println("Примерное начало выполнения заказа: " + order.getPlannedCompletionDateTime().format(dateTimeFormatter));
        }
        System.out.println();
    }

    public void acceptOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                order.acceptOrder();
                order.setCompletionDateTime(LocalDateTime.now());
                order.setEndDateTime(LocalDateTime.now().plus(order.getDurationHours()));
                System.out.println("Заказ №" + id + " принят и выполняется\n");
            }
        }
    }

    public void closeOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                order.closeOrder();
                System.out.println("Заказ №" + id + " закрыт\n");
                return;
            }
        }
        System.out.println("Заказ №" + id + " не найден\n");
    }

    public void cancelOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                order.cancelOrder();
                System.out.println("Заказ №" + id + " отменен\n");
                return;
            }
        }
        System.out.println("Заказ №" + id + " не найден\n");
    }

    public void shiftOrders(Duration time) {
        for (Order order : orders) {
            order.shiftTime(time);
        }
        System.out.println("Изменено время выполнения всех заказов на " + time.toHours() + " час(а/ов) " + time.toMinutesPart() + " минут\n");
    }

    public void deleteOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                orders.remove(order);
                order.deleteOrder();
                System.out.println("Заказ №" + id + " удален\n");
                return;
            }
        }
        System.out.println("Заказ №" + id + " не найден\n");
    }

    public void findOrderByMechanicId(int mechanicId) {
        for (Order order : orders) {
            if (order.getMechanic().getIndex() == mechanicId) {
                System.out.println("Заказ №" + order.getIndex() + ":\n" +
                        "Статус: " + order.getStatus() + "\n" +
                        "Механик: " + order.getMechanic().getName() + " " + order.getMechanic().getSurname() + "\n" +
                        "Название машины: " + order.getCarName() + "\n" +
                        "Место в гараже: " + order.getGaragePlace().getPlaceNumber() + "\n" +
                        "Дата подачи заявки: " + order.getSubmissionDateTime().format(dateTimeFormatter) + "\n" +
                        "Длительность: " + order.getDurationHours().toHours() + " ч. " + order.getDurationHours().toMinutesPart() + " мин.\n" +
                        (order.getCompletionDateTime() != null ? "Начало выполнения заказа: " + order.getCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                        (order.getEndDateTime() != null ? "Конец выполнения: " + order.getEndDateTime().format(dateTimeFormatter) + "\n" : "") +
                        "Цена: " + order.getPrice() + " руб.\n");
                return;
            }
            System.out.println("У данного мастера в данный момент нет заказов");
            return;
        }
    }

//    public void setPlannedCompletionAndEndDateTime(int orderId, LocalDateTime plannedCompletionDateTime) {
//        for (Order order : orders) {
//            if (order.getIndex() == orderId) {
//                order.setPlannedCompletionDateTime(plannedCompletionDateTime);
//                order.setEndDateTime(plannedCompletionDateTime.plus(order.getDurationHours()));
//                return;
//            }
//        }
//    }

    public LocalDateTime getEndDateTimeLastOrder() {
        if (!orders.isEmpty()) {
            return orders.get(orders.size() - 1).getEndDateTime();
        }
        return null;
    }

    public void showOrders(String status) {
        System.out.println("Заказы со статусом '" + status + "':\n");
        for (Order order : orders) {
            if (order.getStatus().equals(status)) {
                System.out.println("Заказ №" + order.getIndex() + ":\n" +
                        "Статус: " + order.getStatus() + "\n" +
                        "Механик: " + order.getMechanic().getName() + " " + order.getMechanic().getSurname() + "\n" +
                        "Название машины: " + order.getCarName() + "\n" +
                        "Место в гараже: " + order.getGaragePlace().getPlaceNumber() + "\n" +
                        "Дата подачи заявки: " + order.getSubmissionDateTime().format(dateTimeFormatter) + "\n" +
                        (order.getStatus().equals("Ожидает") ? "Планируемая дата выполнения заказа: " + order.getPlannedCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                        (order.getDurationHours() != null ? "Длительность: " + order.getDurationHours().toHours() + " ч. " + order.getDurationHours().toMinutesPart() + " мин.\n" : "") +
                        (order.getCompletionDateTime() != null ? "Начало выполнения заказа: " + order.getCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                        (order.getEndDateTime() != null ? "Конец выполнения: " + order.getEndDateTime().format(dateTimeFormatter) + "\n" : "") +
                        "Цена: " + order.getPrice() + " руб.\n");
            }
        }
        System.out.println();
    }

    public void showOrders() {
        System.out.println("Заказы:");
        for (Order order : orders) {
            System.out.println("Заказ №" + order.getIndex() + ":\n" +
                    "Статус: " + order.getStatus() + "\n" +
                    "Механик: " + order.getMechanic().getName() + " " + order.getMechanic().getSurname() + "\n" +
                    "Название машины: " + order.getCarName() + "\n" +
                    "Место в гараже: " + order.getGaragePlace().getPlaceNumber() + "\n" +
                    "Дата подачи заявки: " + order.getSubmissionDateTime().format(dateTimeFormatter) + "\n" +
                    (order.getStatus().equals("Ожидает") ? "Планируемая дата выполнения заказа: " + order.getPlannedCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                    (order.getDurationHours() != null ? "Длительность: " + order.getDurationHours().toHours() + " ч. " + order.getDurationHours().toMinutesPart() + " мин.\n" : "") +
                    (order.getCompletionDateTime() != null ? "Начало выполнения заказа: " + order.getCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                    (order.getEndDateTime() != null ? "Конец выполнения: " + order.getEndDateTime().format(dateTimeFormatter) + "\n" : "") +
                    "Цена: " + order.getPrice() + " руб.\n");
        }
        System.out.println();
    }


    public void sortOrdersByDateOfSubmission() {
        orders.sort(Comparator.comparing(Order::getSubmissionDateTime));
    }

    public void sortOrdersByDateOfSubmissionReversed() {
        orders.sort(Comparator.comparing(Order::getSubmissionDateTime).reversed());
    }

    public void sortOrdersByDateOfCompletion() {
        orders.sort(
                Comparator.comparing(
                        Order::getCompletionDateTime,
                        Comparator.nullsLast(Comparator.naturalOrder()))
        );
    }

    public void sortOrdersByDateOfCompletionReversed() {
        orders.sort(
                Comparator.comparing(
                        Order::getCompletionDateTime,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed()
        );
    }

    public void sortOrdersByPrice() {
        orders.sort(Comparator.comparing(Order::getPrice));
    }

    public void sortOrdersByPriceReversed() {
        orders.sort(Comparator.comparing(Order::getPrice).reversed());
    }

}


