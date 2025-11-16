package com.senla.task1.service;

import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MechanicService {

    private static MechanicService instance;
    private final List<Mechanic> mechanicList = new ArrayList<>();

    private MechanicService() {
    }

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
        boolean removed = mechanicList.removeIf(mechanic -> mechanic.getIndex() == id);

        if (removed) {
            System.out.println("Механик №" + id + " удален");
        } else {
            System.out.println("Такого механика нет");
        }
    }

    public Mechanic findMechanicById(int id) {
        return mechanicList.stream().filter(mechanic -> mechanic.getIndex() == id)
                .findFirst()
                .orElse(null);
    }

    public void showAllMechanic() {
        mechanicList.forEach(mechanic ->
                System.out.println("Механик №" + mechanic.getIndex() + " " +
                        mechanic.getName() + " " +
                        mechanic.getSurname() +
                        ". Лет опыта: " + mechanic.getExperience() + ". " +
                        (!mechanic.isBusy() ? "Механик не занят" : "Механик занят")));
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
        return orders.stream()
                .filter(order -> !order.getStatus().equals("Отменен") && !order.getStatus().equals("Удален"))
                .filter(order -> order.getMechanic().equals(mechanic))
                .filter(order -> order.getSubmissionDateTime() != null && order.getPlannedCompletionDateTime() != null)
                .noneMatch(order -> {
                    LocalDateTime start = order.getSubmissionDateTime();
                    LocalDateTime end = order.getPlannedCompletionDateTime();
                    return !end.isBefore(startDate) && !start.isAfter(endDate);
                });
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
                String name = parts[1].trim();
                String surname = parts[2].trim();
                double experience = Double.parseDouble(parts[3].trim().replace(',', '.'));
                boolean isBusy = Boolean.parseBoolean(parts[4].trim());

//              Если механик уже существует, обновляем, иначе создаем нового
                if (findMechanicById(id) != null) {
                    updateMechanic(id, name, surname, experience, isBusy);
                } else {
                    Mechanic mechanic = new Mechanic(id, name, surname, experience, isBusy);
                    addMechanic(mechanic);
                }
            }
            System.out.println("Данные успешно экспортированы из " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось найти файл");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportToCSV(String filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            bufferedWriter.write("id;name;surname;experienceYears;isBusy");
            bufferedWriter.newLine();

            String lines = mechanicList.stream().map(mechanic ->
                            String.format("%d;%s;%s;%.1f;%b",
                                    mechanic.getIndex(),
                                    mechanic.getName(),
                                    mechanic.getSurname(),
                                    mechanic.getExperience(),
                                    mechanic.isBusy()))
                    .collect(Collectors.joining(System.lineSeparator()));

            bufferedWriter.write(lines);
            System.out.println("Данные успешно экспортированы в " + filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void updateMechanic(int id, String name, String surname, double experience, boolean isBusy) {
        Mechanic mechanic = findMechanicById(id);
        mechanic.setName(name);
        mechanic.setSurname(surname);
        mechanic.setExperience(experience);
        mechanic.setBusy(isBusy);
        System.out.println("Механик № " + id + " обновлен");
    }

}
