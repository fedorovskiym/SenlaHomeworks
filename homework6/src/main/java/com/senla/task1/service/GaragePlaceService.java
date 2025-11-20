package com.senla.task1.service;

import com.senla.task1.exceptions.ExceptionHandler;
import com.senla.task1.exceptions.GaragePlaceException;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GaragePlaceService {

    private static GaragePlaceService instance;
    private final List<GaragePlace> placeList = new ArrayList<>();

    public GaragePlaceService() {
    }

    public static GaragePlaceService getInstance() {
        if (instance == null) {
            instance = new GaragePlaceService();
        }
        return instance;
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

        if(!removed) {
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
                int placeNumber = Integer.parseInt(parts[0].trim());
                boolean isEmpty = Boolean.parseBoolean(parts[1].trim());

//              Если гаражное место существует, обновляем статус, иначе добавляем новое
                if (isGaragePlaceExists(placeNumber)) {
                    findPlaceByNumber(placeNumber).setEmpty(isEmpty);
                } else {
                    addGaragePlace(placeNumber);
                }
            }
            System.out.println("Данные успешно экспортированы из " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось найти файл");
        } catch (IOException e) {
            throw new GaragePlaceException("Ошибка при импорте данных");
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

}
