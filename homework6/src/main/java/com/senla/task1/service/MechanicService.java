package com.senla.task1.service;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.dao.MechanicDAO;
import com.senla.task1.dao.MechanicDAOImpl;
import com.senla.task1.exceptions.MechanicException;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.MechanicSortType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MechanicService {

    private final String folderPath = "data";
    private final String fileName = "mechanic.bin";
    private final MechanicDAOImpl mechanicDAO;

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис механиков создался");
    }

    @Inject
    public MechanicService(MechanicDAOImpl mechanicDAO) {
        this.mechanicDAO = mechanicDAO;
        registerShutdown();
    }

    public List<Mechanic> findAllMechanic() {
        return mechanicDAO.findAll();
    }

    public void addMechanic(String name, String surname, double experienceYears) {
        Mechanic mechanic = new Mechanic(name, surname, experienceYears);
        mechanicDAO.save(mechanic);
        System.out.println("Добавлен механик " + name + " " + surname + ". Опыт: " + experienceYears + " лет/год(а/ов)");
    }

    public void removeMechanicById(Integer id) {
        mechanicDAO.delete(id);
        System.out.println("Механик №" + id + " удален");
    }

    public Mechanic findMechanicById(Integer id) {
        return mechanicDAO.findById(id).orElseThrow(() -> new MechanicException(
                "Механик с id - " + id + " не существует"
        ));
    }

    public void showAllMechanic(List<Mechanic> mechanicList) {
        mechanicList.forEach(mechanic ->
                System.out.println("Механик №" + mechanic.getId() + " " +
                        mechanic.getName() + " " +
                        mechanic.getSurname() +
                        ". Лет опыта: " + mechanic.getExperience() + ". " +
                        (!mechanic.isBusy() ? "Механик не занят" : "Механик занят")));
    }

    public void showSortedMechanicByAlphabet(boolean flag) {
        List<Mechanic> sortedList = mechanicDAO.sortBy(MechanicSortType.ALPHABET.getDisplayName(), flag);
        showAllMechanic(sortedList);
    }

    public void showSortedMechanicByBusy() {
        List<Mechanic> sortedList = mechanicDAO.sortBy(MechanicSortType.BUSY.getDisplayName(), true);
        showAllMechanic(sortedList);
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
                    Integer id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String surname = parts[2].trim();
                    double experience = Double.parseDouble(parts[3].trim().replace(',', '.'));
                    boolean isBusy = Boolean.parseBoolean(parts[4].trim());

                    if (isMechanicExists(id)) {
                        updateMechanic(id, name, surname, experience, isBusy);
                    } else {
                        addMechanic(name, surname, experience);
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

            String lines = findAllMechanic().stream().map(mechanic ->
                            String.format("%d;%s;%s;%.1f;%b",
                                    mechanic.getId(),
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

    private void updateMechanic(Integer id, String name, String surname, double experience, boolean isBusy) {
        Mechanic mechanic = findMechanicById(id);
        mechanic.setName(name);
        mechanic.setSurname(surname);
        mechanic.setExperience(experience);
        mechanic.setBusy(isBusy);
        mechanicDAO.update(mechanic);
        System.out.println("Механик № " + id + " обновлен");
    }

    public boolean isMechanicExists(Integer id) {
        return mechanicDAO.checkIsMechanicExists(id);
    }

    public void save() {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(findAllMechanic());
                System.out.println("Состояние механиков сохранено");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сериализации файла");
        }
    }

//    private void load() {
//        File file = new File(folderPath, fileName);
//        if (!file.exists()) return;
//
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//            List<Mechanic> loadedList = (List<Mechanic>) ois.readObject();
//            mechanicList.clear();
//            mechanicList.addAll(loadedList);
//        } catch (IOException | ClassNotFoundException e) {
//            System.out.println("Ошибка при десериализации файла");
//        }
//    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }
}
