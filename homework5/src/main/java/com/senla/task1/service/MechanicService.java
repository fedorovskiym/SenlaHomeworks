package com.senla.task1.service;

import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.OrderStatus;
import org.w3c.dom.ls.LSOutput;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MechanicService {

    private static MechanicService instance;
    private final List<Mechanic> mechanicList = new ArrayList<>();

    private MechanicService() {}

    public static MechanicService getInstance() {
        if (instance == null) {
            instance = new MechanicService();
        }
        return instance;
    }

    public List<Mechanic> getMechanicList() {
        return mechanicList;
    }

    public void addMechanic(Mechanic mechanic) {
        mechanicList.add(mechanic);
        System.out.println("Добавлен механик " + mechanic.getName() + " " + mechanic.getSurname() + ". Опыт: " + mechanic.getExperience() + " лет/год(а/ов)");
    }

    public void removeMechanicById(int id) {
        for (Mechanic mechanic : mechanicList) {
            if (mechanic.getIndex() == id) {
                mechanicList.remove(mechanic);
                System.out.println("Удален механик " + mechanic.getName());
                return;
            }
        }
        System.out.println("Механика с таким номером нет");
    }

    public Mechanic findMechanicById(int id) {
        for (Mechanic mechanic : mechanicList) {
            if (mechanic.getIndex() == id) {
                return mechanic;
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
        sortMechanicsById();
    }

    public boolean isMechanicAvailable(Mechanic mechanic, List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        for (Order order : orders) {

            if (!order.getStatus().equals(OrderStatus.CANCEL) && !order.getStatus().equals(OrderStatus.DELETED)) {

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

}
