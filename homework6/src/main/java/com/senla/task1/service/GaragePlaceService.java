package com.senla.task1.service;

import com.senla.task1.annotations.PostConstruct;
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

    private final List<GaragePlace> placeList = new ArrayList<>();
    private final String folderPath = "data";
    private final String fileName = "garage_places.bin";

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис гаражных мест создался");
    }

    public GaragePlaceService() {
        load();
        registerShutdown();
    }

    public List<GaragePlace> getPlaceList() {
        return placeList;
    }

    public GaragePlace findPlaceByNumber(int placeNumber) {
        return placeList.stream()
                .filter(garagePlace -> garagePlace.getPlaceNumber() == placeNumber)
                .findFirst()
                .orElseThrow(() -> new GaragePlaceException(
                        "Место в гараже № " + placeNumber + " уже существует"
                ));
    }


    public void showFreeGaragePlaces() {
        placeList.stream().filter(GaragePlace::isEmpty).forEach(garagePlace ->
                System.out.println("Место №" + garagePlace.getPlaceNumber() + " свободно")
        );

        System.out.println();

    }

    public void addGaragePlace(int number) {
        placeList.add(new GaragePlace(number));
        System.out.println("Добавлено место в гараж №" + number);
    }

    public void removeGaragePlace(int number) {
        boolean removed = placeList.removeIf(place -> place.getPlaceNumber() == number);

        if (!removed) {
            throw new GaragePlaceException("Места в гараже № " + number + " нет");
        }

        System.out.println("Место в гараже №" + number + " удалено");

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

//              Если гаражное место существует, обновляем статус, иначе добавляем новое
                    if (isGaragePlaceExists(placeNumber)) {
                        findPlaceByNumber(placeNumber).setEmpty(isEmpty);
                    } else {
                        addGaragePlace(placeNumber);
                    }
                }
            }
            System.out.println("Данные успешно экспортированы из " + resourceName);
        } catch (IOException e) {
            throw new MechanicException("Ошибка при импорте данных механиков");
        }
    }

    public void exportToCSV(String filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            bufferedWriter.write("placeNumber;isEmpty");
            bufferedWriter.newLine();

            String lines = placeList.stream().map(garagePlace ->
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
        return placeList.stream().anyMatch(garagePlace -> garagePlace.getPlaceNumber() == placeNumber);
    }

    public void save() {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(placeList);
                System.out.println("Состояние мест в гараже сохранено");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сериализации файла");
        }
    }

    private void load() {
        File file = new File(folderPath, fileName);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<GaragePlace> loadedList = (List<GaragePlace>) ois.readObject();
            placeList.clear();
            placeList.addAll(loadedList);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при десериализации файла");
        }
    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

}
