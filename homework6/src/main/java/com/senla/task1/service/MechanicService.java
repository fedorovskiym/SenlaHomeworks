package com.senla.task1.service;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.dao.impl.jpa.MechanicJpaDAOImpl;
import com.senla.task1.exceptions.MechanicException;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.MechanicSortType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MechanicService {

    private final String folderPath = "data";
    private final String fileName = "mechanic.bin";
    private final MechanicJpaDAOImpl mechanicDAO;
    private static final Logger logger = LogManager.getLogger(MechanicService.class);

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис механиков создался");
    }

    @Inject
    public MechanicService(MechanicJpaDAOImpl mechanicDAO) {
        this.mechanicDAO = mechanicDAO;
        registerShutdown();
    }

    public List<Mechanic> findAllMechanic() {
        return mechanicDAO.findAll();
    }

    public void addMechanic(String name, String surname, Double experienceYears) {
        logger.info("Обработка добавления нового механика");
        Mechanic mechanic = new Mechanic(name, surname, experienceYears);
        mechanicDAO.save(mechanic);
        logger.info("Добавлен новый механик {}", mechanic);
    }

    public void removeMechanicById(Integer id) {
        logger.info("Обработка удаления механика № {}", id);
        Mechanic mechanic = mechanicDAO.findById(id).orElse(null);
        mechanicDAO.delete(mechanic);
        logger.info("Механик № {} удален", id);
    }

    public Mechanic findMechanicById(Integer id) {
        return mechanicDAO.findById(id).orElseThrow(() -> new MechanicException(
                "Механик с id - " + id + " не существует"
        ));
    }

    public void showAllMechanic(List<Mechanic> mechanicList) {
        mechanicList.forEach(mechanic -> System.out.println(formatMechanic(mechanic)));
    }

    public void showSortedMechanicByAlphabet(Boolean flag) {
        logger.info("Обработка сортировки механиков по алфавиту");
        List<Mechanic> sortedList = mechanicDAO.sortBy(MechanicSortType.ALPHABET.getDisplayName(), flag);
        logger.info("Механики отсортированы по алфавиту");
        showAllMechanic(sortedList);
    }

    public void showSortedMechanicByBusy() {
        logger.info("Обработка сортировки механиков по занятости");
        List<Mechanic> sortedList = mechanicDAO.sortBy(MechanicSortType.BUSY.getDisplayName(), true);
        logger.info("Механики отсортированы по занятости");
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
        logger.info("Обработка импорта данных механиков из файла {}", resourceName);
        List<Mechanic> mechanicsToSaveOrUpdate = new ArrayList<>();
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

                    Mechanic mechanic = new Mechanic(id, name, surname, experience, isBusy);
                    mechanicsToSaveOrUpdate.add(mechanic);
                }
            }

            // Транзакция
            mechanicDAO.importWithTransaction(mechanicsToSaveOrUpdate);

            logger.info("Данные успешно импортированы из файла {}", resourceName);
        } catch (IOException e) {
            logger.error("Ошибка при импорте данных механиков из файла {}", resourceName);
            throw new RuntimeException(e);
        }
    }

    public void exportToCSV(String filePath) {
        logger.info("Обработка экспорта данных механиков в файл {}", filePath);
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
            logger.info("Данные успешно записаны в файл {}", filePath);
        } catch (IOException e) {
            logger.error("Ошибка при экспорте данных в файл {}", filePath);
            throw new RuntimeException(e);
        }
    }

    public void updateMechanic(Mechanic mechanic) {
        mechanicDAO.update(mechanic);
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

    public String formatMechanic(Mechanic mechanic) {
        return String.format(
                """
                        Механик №%d: %s %s
                        Лет опыта: %.2f
                        Статус: %s
                        """,
                mechanic.getId(),
                mechanic.getName(),
                mechanic.getSurname(),
                mechanic.getExperience(),
                mechanic.isBusy() ? "Механик занят" : "Механик не занят"
        );
    }
}
