package com.senla.task1.service;

import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.exceptions.MechanicException;
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

    private final List<Mechanic> mechanicList = new ArrayList<>();
    private final String folderPath = "data";
    private final String fileName = "mechanic.bin";

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис механиков создался");
    }

    public MechanicService() {
        load();
        registerShutdown();
    }

    public List<Mechanic> getMechanicList() {
        return mechanicList;
    }

    public void addMechanic(String name, String surname, double experienceYears) {
        mechanicList.add(new Mechanic(name, surname, experienceYears));
        System.out.println("Добавлен механик " + name + " " + surname + ". Опыт: " + experienceYears + " лет/год(а/ов)");
    }

    public void addMechanic(int id, String name, String surname, double experienceYears, boolean isBusy) {
        mechanicList.add(new Mechanic(id, name, surname, experienceYears, isBusy));
        System.out.println("Добавлен механик " + name + " " + surname + ". Опыт: " + experienceYears + " лет/год(а/ов)");
    }

    public void removeMechanicById(int id) {
        boolean removed = mechanicList.removeIf(mechanic -> mechanic.getIndex() == id);

        if (!removed) {
            throw new MechanicException("Механика №" + id + " не существует");
        }

        System.out.println("Механик №" + id + " удален");
    }

    public Mechanic findMechanicById(int id) {
        return mechanicList.stream().filter(mechanic -> mechanic.getIndex() == id)
                .findFirst()
                .orElseThrow(() -> new MechanicException("Механика №" + id + " не существует"));
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

    public void importFromCSV(String resourceName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("csv/".concat(resourceName))) {

            if (inputStream == null) {
                throw new FileNotFoundException("Ресурс не найден: " + resourceName);
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
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

//                  Если механик уже существует, обновляем, иначе создаем нового
                    if (isMechanicExists(id)) {
                        updateMechanic(id, name, surname, experience, isBusy);
                    } else {
                        addMechanic(id, name, surname, experience, isBusy);
                    }
                }
            }
            System.out.println("Данные успешно импортированы из " + resourceName);
        } catch (IOException e) {
            throw new MechanicException("Ошибка при импорте данных механиков");
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
            throw new MechanicException("Ошибка при экспорте данных");
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

    public boolean isMechanicExists(int id) {
        return mechanicList.stream().anyMatch(mechanic -> mechanic.getIndex() == id);
    }

    public void save() {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(mechanicList);
                System.out.println("Состояние механиков сохранено");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сериализации файла");
        }
    }

    private void load() {
        File file = new File(folderPath, fileName);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Mechanic> loadedList = (List<Mechanic>) ois.readObject();
            mechanicList.clear();
            mechanicList.addAll(loadedList);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при десериализации файла");
        }
    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }
}
