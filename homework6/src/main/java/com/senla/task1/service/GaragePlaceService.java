package com.senla.task1.service;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.dao.impl.jdbc.GaragePlaceDAOImpl;
import com.senla.task1.dao.impl.jpa.GaragePlaceJpaDAOImpl;
import com.senla.task1.exceptions.GaragePlaceException;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Order;
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

public class GaragePlaceService {

    private final String folderPath = "data";
    private final String fileName = "garage_places.bin";
    private final GaragePlaceJpaDAOImpl garagePlaceDAO;
    private static final Logger logger = LogManager.getLogger(GaragePlaceService.class);

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис гаражных мест создался");
    }

    @Inject
    public GaragePlaceService(GaragePlaceJpaDAOImpl garagePlaceDAO) {
        this.garagePlaceDAO = garagePlaceDAO;
//        load();
        registerShutdown();
    }

    public List<GaragePlace> findAllGaragePlace() {
        return garagePlaceDAO.findAll();
    }

    public GaragePlace findPlaceByNumber(Integer placeNumber) {
        return garagePlaceDAO.findByPlaceNumber(placeNumber).orElseThrow(() -> new GaragePlaceException(
                "Место в гараже № " + placeNumber + " не существует"
        ));
    }


    public void findFreeGaragePlaces() {
        logger.info("Обработка поиска всех мест в гараже");
        List<GaragePlace> freeGaragePlaces = garagePlaceDAO.findFreeGaragePlaces();
        freeGaragePlaces.forEach(garagePlace -> showGaragePlaces(garagePlace));
        logger.info("Выведены места в гараже и их статус");
    }

    public void addGaragePlace(Integer number) {
        logger.info("Обработка добавления гаражного места № {}", number);
        GaragePlace garagePlace = new GaragePlace(number);
        garagePlaceDAO.save(garagePlace);
        logger.info("Место № {} успешно добавлено", number);
    }

    public void removeGaragePlace(Integer id) {
        logger.info("Обработка удаления гаражного места № {}", id);
        GaragePlace garagePlace = garagePlaceDAO.findById(id).orElseThrow(() -> new GaragePlaceException(
                "Места в гараже c id " + id + " не найдено"
        ));
        garagePlaceDAO.delete(garagePlace);
        logger.info("Место в гараже № {} удалено", id);
    }

    public boolean isGaragePlaceAvailable(GaragePlace garagePlace, List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        return orders.stream()
                .filter(order -> !order.getStatus().equals("Отменен") && !order.getStatus().equals("Удален"))
                .filter(order -> order.getGaragePlace().equals(garagePlace))
                .filter(order -> order.getSubmissionDateTime() != null && order.getPlannedCompletionDateTime() != null)
                .noneMatch(order -> {
                    LocalDateTime start = order.getSubmissionDateTime();
                    LocalDateTime end = order.getPlannedCompletionDateTime();
                    return !end.isBefore(startDate) && !start.isAfter(endDate);
                });
    }

    public void importFromCSV(String resourceName) {
        logger.info("Обработка импорта данных гаражных мест из файла {}", resourceName);
        List<GaragePlace> garagePlacesToSaveOrUpdate = new ArrayList<>();

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
                    int placeNumber = Integer.parseInt(parts[1].trim());
                    boolean isEmpty = Boolean.parseBoolean(parts[2].trim());

                    GaragePlace garagePlace = new GaragePlace(id, placeNumber, isEmpty);
                    garagePlacesToSaveOrUpdate.add(garagePlace);
                }
            }
            // Транзакция
            garagePlaceDAO.importWithTransaction(garagePlacesToSaveOrUpdate);
            logger.info("Данные успешно импортированы из файла {}", resourceName);
        } catch (IOException e) {
            logger.error("Ошибка при импорте данных из файла {}", resourceName);
            throw new RuntimeException(e);
        }
    }

    public void updateGaragePlace(GaragePlace garagePlace) {
        garagePlaceDAO.update(garagePlace);
    }

    public void exportToCSV(String filePath) {
        logger.info("Обработка экспорта данных гаражных мест в файл {}", filePath);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            bufferedWriter.write("placeNumber;isEmpty");
            bufferedWriter.newLine();

            String lines = findAllGaragePlace().stream().map(garagePlace ->
                            String.format("%d%b",
                                    garagePlace.getPlaceNumber(),
                                    garagePlace.isEmpty()))
                    .collect(Collectors.joining(System.lineSeparator()));
            bufferedWriter.write(lines);
            logger.info("Данные гаражных мест успешно записаны в файл {}", filePath);
        } catch (IOException e) {
            logger.error("Ошибка при экспорте данных гаражных мест в файл {}", filePath);
        }
    }

    public boolean isGaragePlaceExists(Integer placeNumber) {
        return garagePlaceDAO.checkIsPlaceNumberExists(placeNumber);
    }

    public void save() {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(garagePlaceDAO.findAll());
                System.out.println("Состояние мест в гараже сохранено");
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
//            List<GaragePlace> loadedList = (List<GaragePlace>) ois.readObject();
//            placeList.clear();
//            placeList.addAll(loadedList);
//        } catch (IOException | ClassNotFoundException e) {
//            System.out.println("Ошибка при десериализации файла");
//        }
//    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

    public void showGaragePlaces(GaragePlace garagePlace) {
        System.out.println("Id: " + garagePlace.getId() + "\n" +
                "Номер места: " + garagePlace.getPlaceNumber() + "\n" +
                "Статус: " + (garagePlace.isEmpty() ? "Не занято" : "Занято"));
    }
}
