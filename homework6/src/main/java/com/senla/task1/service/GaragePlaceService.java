package com.senla.task1.service;

import com.senla.task1.config.AutoServiceConfig;
import com.senla.task1.exceptions.GaragePlaceException;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Order;
import com.senla.task1.repository.GaragePlaceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GaragePlaceService {

    private final GaragePlaceRepository garagePlaceRepository;
    private final AutoServiceConfig autoServiceConfig;
    private static final Logger logger = LogManager.getLogger(GaragePlaceService.class);

    @Autowired
    public GaragePlaceService(@Qualifier("garagePlaceJpaDAO") GaragePlaceRepository garagePlaceRepository, AutoServiceConfig autoServiceConfig) {
        this.autoServiceConfig = autoServiceConfig;
        this.garagePlaceRepository = garagePlaceRepository;
    }

    @Transactional(readOnly = true)
    public List<GaragePlace> findAllGaragePlace() {
        return garagePlaceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public GaragePlace findPlaceByNumber(Integer placeNumber) {
        return garagePlaceRepository.findByPlaceNumber(placeNumber).orElseThrow(() -> new GaragePlaceException(
                "Место в гараже № " + placeNumber + " не существует"
        ));
    }

    @Transactional(readOnly = true)
    public List<GaragePlace> findFreeGaragePlaces() {
        logger.info("Обработка поиска всех мест в гараже");
        List<GaragePlace> freeGaragePlaces = garagePlaceRepository.findFreeGaragePlaces();
        logger.info("Получены места в гараже и их статус");
        return freeGaragePlaces;
    }

    @Transactional
    public void addGaragePlace(Integer number) {
        if (!autoServiceConfig.isAllowAddGaragePlace()) {
            throw new GaragePlaceException("Добавление новых мест в гараже отключено!");
        }
        logger.info("Обработка добавления гаражного места № {}", number);
        GaragePlace garagePlace = new GaragePlace(number);
        garagePlaceRepository.save(garagePlace);
        logger.info("Место № {} успешно добавлено", number);
    }

    @Transactional
    public void removeGaragePlace(Integer id) {
        if (!autoServiceConfig.isAllowDeleteGaragePlace()) {
            throw new GaragePlaceException("Удаление мест в гараже отключено!");
        }
        logger.info("Обработка удаления гаражного места № {}", id);
        GaragePlace garagePlace = garagePlaceRepository.findById(id).orElseThrow(() -> new GaragePlaceException(
                "Места в гараже c id " + id + " не найдено"
        ));
        garagePlaceRepository.delete(garagePlace);
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

    @Transactional
    public void importFromCSV(String resourceName) {
        logger.info("Обработка импорта данных гаражных мест из файла {}", resourceName);

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
                    if (isGaragePlaceExists(placeNumber)) {
                        garagePlaceRepository.update(garagePlace);
                    } else {
                        garagePlaceRepository.save(garagePlace);
                    }
                }
            }
            logger.info("Данные успешно импортированы из файла {}", resourceName);
        } catch (IOException e) {
            logger.error("Ошибка при импорте данных из файла {}", resourceName);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateGaragePlace(GaragePlace garagePlace) {
        garagePlaceRepository.update(garagePlace);
    }

    @Transactional
    public String exportToCSV() {
        logger.info("Обработка экспорта данных гаражных мест в файл");
        List<GaragePlace> garagePlaces = garagePlaceRepository.findAll();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id;placeNumber;isEmpty").append(System.lineSeparator());
        garagePlaces.forEach(garagePlace -> stringBuilder.append(String.format("%d;%d;%b",
                        garagePlace.getId(),
                        garagePlace.getPlaceNumber(),
                        garagePlace.isEmpty()))
                .append(System.lineSeparator()));
        logger.info("Данные гаражных мест успешно записаны в файл");
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public boolean isGaragePlaceExists(Integer placeNumber) {
        return garagePlaceRepository.checkIsPlaceNumberExists(placeNumber);
    }
}
