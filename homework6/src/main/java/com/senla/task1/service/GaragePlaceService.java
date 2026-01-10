package com.senla.task1.service;

import com.senla.task1.annotations.FieldInject;
import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.dao.GaragePlaceDAO;
import com.senla.task1.dao.GaragePlaceDAOImpl;
import com.senla.task1.exceptions.GaragePlaceException;
import com.senla.task1.exceptions.MechanicException;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GaragePlaceService {

    private final String folderPath = "data";
    private final String fileName = "garage_places.bin";
    private final GaragePlaceDAOImpl garagePlaceDAO;

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис гаражных мест создался");
    }

    @Inject
    public GaragePlaceService(GaragePlaceDAOImpl garagePlaceDAO) {
        this.garagePlaceDAO = garagePlaceDAO;
//        load();
        registerShutdown();
    }


    public List<GaragePlace> findAllGaragePlace() {
        return garagePlaceDAO.findAll();
    }

    public GaragePlace findPlaceByNumber(int placeNumber) {
        return garagePlaceDAO.findByPlaceNumber(placeNumber).orElseThrow(() -> new GaragePlaceException(
                "Место в гараже № " + placeNumber + " не существует"
        ));
    }


    public void findFreeGaragePlaces() {
        List<GaragePlace> freeGaragePlaces = garagePlaceDAO.findFreeGaragePlaces();
        freeGaragePlaces.forEach(garagePlace -> showGaragePlaces(garagePlace));
    }

    public void addGaragePlace(int number) {
        GaragePlace garagePlace = new GaragePlace(number);
        garagePlaceDAO.save(garagePlace);
        System.out.println("Добавлено место в гараж №" + number);
    }

    public void removeGaragePlace(int id) {
        garagePlaceDAO.delete(id);
        System.out.println("Место в гараже №" + id + " удалено");
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
                    int placeNumber = Integer.parseInt(parts[0].trim());
                    boolean isEmpty = Boolean.parseBoolean(parts[1].trim());

                    if (isGaragePlaceExists(placeNumber)) {
                        updateGaragePlace(placeNumber, isEmpty);
                    } else {
                        addGaragePlace(placeNumber);
                    }
                }
            }
            System.out.println("Данные успешно экспортированы из " + resourceName);
        } catch (IOException e) {
            throw new GaragePlaceException("Ошибка при импорте данных гаражных мест");
        }
    }

    private void updateGaragePlace(int placeNumber, boolean isEmpty) {
        GaragePlace garagePlace = findPlaceByNumber(placeNumber);
        garagePlace.setEmpty(isEmpty);
        garagePlaceDAO.update(garagePlace);
    }

    public void exportToCSV(String filePath) {
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
            System.out.println("Данные успешно экспортированы в " + filePath);

        } catch (IOException e) {
            throw new GaragePlaceException("Ошибка при экспорте данных");
        }
    }

    public boolean isGaragePlaceExists(int placeNumber) {
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
