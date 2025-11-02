package com.senla.task1.service;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import org.w3c.dom.ls.LSOutput;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class OrderService {

    private final List<Order> orders = new ArrayList<>();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

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
        System.out.println("Статус заказа: '" + order.getStatus() + "'");
        if (order.getPlannedCompletionDateTime() != null) {
            System.out.println("Примерное начало выполнения заказа: " + order.getPlannedCompletionDateTime().format(dateTimeFormatter));
        }
        System.out.println();
    }

    public void acceptOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                if (order.getStatus().equals("Принят")) {
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
        System.out.println("Заказ не найден\n");
    }

    public void closeOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                order.closeOrder();
                System.out.println("Заказ №" + id + " закрыт\n");
                acceptOrder(id + 1);
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

    // Вывод заказа по айдишнку механика
    public void findOrderByMechanicId(int mechanicId) {
        for (Order order : orders) {
            if (order.getMechanic().getIndex() == mechanicId) {
                System.out.println("Заказ №" + order.getIndex() + ":\n" +
                        "Статус: " + order.getStatus() + "\n" +
                        "Механик: " + order.getMechanic().getName() + " " + order.getMechanic().getSurname() + "\n" +
                        "Название машины: " + order.getCarName() + "\n" +
                        "Место в гараже: " + order.getGaragePlace().getPlaceNumber() + "\n" +
                        "Дата подачи заявки: " + order.getSubmissionDateTime().format(dateTimeFormatter) + "\n" +
                        "Длительность: " + order.getDuration().toHours() + " ч. " + order.getDuration().toMinutesPart() + " мин.\n" +
                        (order.getCompletionDateTime() != null ? "Начало выполнения заказа: " + order.getCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                        (order.getEndDateTime() != null ? "Конец выполнения: " + order.getEndDateTime().format(dateTimeFormatter) + "\n" : "") +
                        "Цена: " + order.getPrice() + " руб.\n");
                return;
            }
            System.out.println("У данного мастера в данный момент нет заказов");
            return;
        }
    }

    // Получение времени окончания последнего активного заказа
    public LocalDateTime getEndDateTimeLastOrder() {
        if(getLastActiveOrder() == null) {
            return null;
        }
        return getLastActiveOrder().getEndDateTime();
    }

    // Получение последнего заказа со статусом 'Ожидает' или 'Принят'
    public Order getLastActiveOrder() {
        for (int i = orders.size() - 1; i >= 0; i--) {
            Order order = orders.get(i);
            if (!order.getStatus().equals("Отменен") && !order.getStatus().equals("Удален") && !order.getStatus().equals("Выполнен")) {
                return order;
            }
        }
        return null;
    }

    // Вывод заказов по статусу
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
                        (order.getDuration() != null ? "Длительность: " + order.getDuration().toHours() + " ч. " + order.getDuration().toMinutesPart() + " мин.\n" : "") +
                        (order.getCompletionDateTime() != null ? "Начало выполнения заказа: " + order.getCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                        (order.getEndDateTime() != null ? "Конец выполнения: " + order.getEndDateTime().format(dateTimeFormatter) + "\n" : "") +
                        "Цена: " + order.getPrice() + " руб.\n");
            }
        }
        System.out.println();
    }

    // Вывод всех заказов
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
                    (order.getDuration() != null ? "Длительность: " + order.getDuration().toHours() + " ч. " + order.getDuration().toMinutesPart() + " мин.\n" : "") +
                    (order.getCompletionDateTime() != null ? "Начало выполнения заказа: " + order.getCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                    (order.getEndDateTime() != null ? "Конец выполнения: " + order.getEndDateTime().format(dateTimeFormatter) + "\n" : "") +
                    "Цена: " + order.getPrice() + " руб.\n");
        }
        System.out.println();
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


    public void showMechanicByOrderId(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                System.out.println("Механик №" + order.getMechanic().getIndex() + " " +
                        order.getMechanic().getName() + " " +
                        order.getMechanic().getSurname() +
                        ". Лет опыта: " + order.getMechanic().getExperience() + " " +
                        (order.getMechanic().isBusy() ? "Механик не занят" : "Механик занят"));
            }
        }
    }

    // Метод для определения каким способом сортировать и выводить заявки за период времени
    public void findOrdersOverPeriodOfTime(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay, String sortType, boolean flag) {
        LocalDateTime startTime = LocalDateTime.of(fromYear, fromMonth, fromDay, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(toYear, toMonth, toDay, 23, 59);
        System.out.println("Заказы в период с " + startTime.format(dateTimeFormatter) + " по " + endTime.format(dateTimeFormatter));
        System.out.println();
        switch (sortType) {
            case "submission": {
                sortOrdersByDateOfSubmission(flag);
                break;
            }
            case "completion": {
                sortOrdersByDateOfCompletion(flag);
                break;
            }
            case "price": {
                sortOrdersByPrice(flag);
                break;
            }
            default: break;
        }
        showOrdersOverPeriodOfTime(startTime, endTime);
        sortOrdersByDateOfSubmission(true);
    }

    // Вывод заказов за период времени по дате подачи заявки
    public void showOrdersOverPeriodOfTime(LocalDateTime startDate, LocalDateTime endDate) {
        System.out.println("Заказы:");
        for (Order order : orders) {
            if (!order.getStatus().equals("Ожидает") && !order.getStatus().equals("Принят")) {
                if (order.getSubmissionDateTime().isAfter(startDate) && order.getSubmissionDateTime().isBefore(endDate)) {
                    System.out.println("Заказ №" + order.getIndex() + ":\n" +
                            "Статус: " + order.getStatus() + "\n" +
                            "Механик: " + order.getMechanic().getName() + " " + order.getMechanic().getSurname() + "\n" +
                            "Название машины: " + order.getCarName() + "\n" +
                            "Место в гараже: " + order.getGaragePlace().getPlaceNumber() + "\n" +
                            "Дата подачи заявки: " + order.getSubmissionDateTime().format(dateTimeFormatter) + "\n" +
                            "Длительность: " + order.getDuration().toHours() + " ч. " + order.getDuration().toMinutesPart() + " мин.\n" +
                            (order.getCompletionDateTime() != null ? "Начало выполнения заказа: " + order.getCompletionDateTime().format(dateTimeFormatter) + "\n" : "") +
                            "Конец выполнения: " + order.getEndDateTime().format(dateTimeFormatter) + "\n" +
                            "Цена: " + order.getPrice() + " руб.\n");
                }
            }
        }
        System.out.println();
    }

    public void showNearestAvailableDate() {
        System.out.println("Ближайшая свободная дата: " + getEndDateTimeLastOrder().format(dateTimeFormatter));
    }
}


