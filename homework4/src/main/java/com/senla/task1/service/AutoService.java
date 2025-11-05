package com.senla.task1.service;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.service.OrderService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoService {

    private final List<Mechanic> mechanicList = new ArrayList<>();
    private final List<GaragePlace> placeList = new ArrayList<>();
    private final OrderService orderService = new OrderService();

    public void addMechanic(Mechanic mechanic) {
        mechanicList.add(mechanic);
        System.out.println("Добавлен механик " + mechanic.getName() + " " + mechanic.getSurname() + ". Опыт: " + mechanic.getExperience() + " лет/год(а/ов)");
    }

    public void removeMechanic(int id) {
        for (Mechanic mechanic : mechanicList) {
            if (mechanic.getIndex() == id) {
                mechanicList.remove(mechanic);
                System.out.println("Удален механик " + mechanic.getName());
                return;
            }
        }
    }

    public Object findMechanicById(int id) {
        for (Mechanic mechanic : mechanicList) {
            if (mechanic.getIndex() == id) {
                return mechanic;
            }
        }
        return null;
    }

    public Object findPlaceByNumber(int placeNumber) {
        for (GaragePlace garagePlace : placeList) {
            if (garagePlace.getPlaceNumber() == placeNumber) {
                return garagePlace;
            }
        }
        return null;
    }

    public void showAllMechanic() {
        for (Mechanic mechanic : mechanicList) {
            System.out.println("Механик №" + mechanic.getIndex() + " " +
                    mechanic.getName() + " " +
                    mechanic.getSurname() +
                    ". Лет опыта: " + mechanic.getExperience() + ". " +
                    (!mechanic.isBusy() ? "Механик не занят" : "Механик занят"));
        }
        System.out.println();
    }

    public void showFreeGaragePlaces() {
        for (GaragePlace garagePlace : placeList) {
            if (garagePlace.isEmpty()) {
                System.out.println("Место №" + garagePlace.getPlaceNumber() + " свободно");
            }
        }
        System.out.println();
    }

    public void addGaragePlace(int number) {
        placeList.add(new GaragePlace(number));
        System.out.println("Добавлено место в гараж №" + number);
    }

    public void removeGaragePlace(int number) {
        for (GaragePlace place : placeList) {
            if (place.getPlaceNumber() == number) {
                placeList.remove(place);
                System.out.println("Место в гараже №" + number + " удалено");
                return;
            }
        }
    }

    public void createOrder(String carModel, int mechanicId, int placeNumber, double price, long hours, long minutes) {

        if (!mechanicList.contains(findMechanicById(mechanicId)) || !placeList.contains(findPlaceByNumber(placeNumber))) {
            System.out.println("Ошибка: указаны неверные айдишники механика или место в гараже");
            return;
        }

        Mechanic mechanic = (Mechanic) findMechanicById(mechanicId);
        GaragePlace garagePlace = (GaragePlace) findPlaceByNumber(placeNumber);

        if (mechanic.isBusy() || !garagePlace.isEmpty()) {
            System.out.println("Механик или место занято!");
            return;
        }

        Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
        Order order = new Order(carModel, mechanic, garagePlace, duration, price);

        orderService.addOrder(order);
    }

    public void getOrderByMechanicId(int mechanicId) {
        orderService.findOrderByMechanicId(mechanicId);
    }

    public void acceptOrder(int id) {
        orderService.acceptOrder(id);
    }

    public void deleteOrder(int id) {
        orderService.deleteOrder(id);
    }

    public void closeOrder(int id) {
        orderService.closeOrder(id);
    }

    public void cancelOrder(int id) {
        orderService.cancelOrder(id);
    }

    public void shiftOrdersTime(int hours, int minutes) {
        Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
        orderService.shiftOrders(duration);
    }

    public void showOrderByStatus(String status) {
        orderService.showOrders(status);
    }

    public void showAllOrders() {
        orderService.showOrders();
    }

    //  Сортировка заказов по дате подачи заявки (flag - способ отображения (перевернутый или обычный)
    public void showSortedOrdersByDateOfSubmission(boolean flag) {
        orderService.sortOrdersByDateOfSubmission(flag);
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission(true);
    }

    //  Сортировка заказов по дате выполнения (flag - способ отображения (перевернутый или обычный)
    public void showSortedOrdersByDateOfCompletion(boolean flag) {
        orderService.sortOrdersByDateOfCompletion(flag);
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission(true);
    }

    //  Сортировка заказов по цене (flag - способ отображения (перевернутый или обычный)
    public void showSortedOrdersByPrice(boolean flag) {
        orderService.sortOrdersByPrice(flag);
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission(true);
    }

    // Сортировка механиков по алфавиту (flag - способ отображения)
    public void showSortedMechanicByAlphabet(boolean flag) {

        Comparator<Mechanic> comparator = Comparator.comparing(Mechanic::getName);

        if (!flag) {
            comparator = comparator.reversed();
        }

        mechanicList.sort(comparator);
        showAllMechanic();
        sortMechanicsById();
    }

    public void sortMechanicsById() {
        mechanicList.sort(Comparator.comparing(Mechanic::getIndex));
    }

    public void showSortedMechanicByBusy() {
        mechanicList.sort(Comparator.comparing(Mechanic::isBusy));
        showAllMechanic();
    }

    public void getMechanicByOrderId(int id) {
        orderService.showMechanicByOrderId(id);
    }

    //  Вывод заказов за промежуток времени
    public void showOrdersOverPeriodOfTime(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay, String sortType, boolean flag) {
        orderService.findOrdersOverPeriodOfTime(fromYear, fromMonth, fromDay, toYear, toMonth, toDay, sortType, flag);
    }

    //  Получение количества свободных мест на дату
    public void getAvailableSlot(int year, int month, int day) {

        int availableMechanics = 0;
        int availableGaragePlaces = 0;

        List<Order> orders = orderService.getOrders();
        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59);

        for (Mechanic mechanic : mechanicList) {
            if (isMechanicAvailable(mechanic, orders, startDate, endDate)) {
                availableMechanics++;
            }
        }

        for (GaragePlace garagePlace : placeList) {
            if (isGaragePlaceAvailable(garagePlace, orders, startDate, endDate)) {
                availableGaragePlaces++;
            }
        }

        System.out.println("Количество свободных мест в сервисе на " + day + "." + month + "." + year + " - " + Math.min(availableMechanics, availableGaragePlaces));
    }

    //  Проверка свободен ли механик на дату
    public boolean isMechanicAvailable(Mechanic mechanic, List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        for (Order order : orders) {

            if (!order.getStatus().equals("Отменен") && !order.getStatus().equals("Удален")) {

                if (order.getMechanic() != null && order.getMechanic().equals(mechanic)) {
                    LocalDateTime start = order.getSubmissionDateTime();
                    LocalDateTime end = order.getEndDateTime();

                    if (start == null || end == null) continue;

                    if (!end.isBefore(startDate) && !start.isAfter(endDate)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Проверка свободно ли место в гараже на дату
    public boolean isGaragePlaceAvailable(GaragePlace garagePlace, List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        for (Order order : orders) {
            if (order.getStatus().equals("Отменен") || order.getStatus().equals("Удален")) continue;

            if (order.getGaragePlace().equals(garagePlace)) {
                LocalDateTime start = order.getSubmissionDateTime();
                LocalDateTime end = order.getPlannedCompletionDateTime();

                if (start == null || end == null) continue;

                if (!end.isBefore(startDate) && !start.isAfter(endDate)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showNearestAvailableDate() {
        orderService.showNearestAvailableDate();
    }


}
