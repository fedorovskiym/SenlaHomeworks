package com.senla.task1.service;

import com.senla.task1.dto.MechanicDTO;
import com.senla.task1.exceptions.MechanicException;
import com.senla.task1.mapper.MechanicMapper;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.MechanicSortType;
import com.senla.task1.repository.MechanicRepository;
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
import java.util.stream.Collectors;

@Service
public class MechanicService {

    private final MechanicRepository mechanicRepository;
    private final MechanicMapper mechanicMapper;
    private static final Logger logger = LogManager.getLogger(MechanicService.class);

    @Autowired
    public MechanicService(@Qualifier("mechanicJpaDAO") MechanicRepository mechanicRepository, MechanicMapper mechanicMapper) {
        this.mechanicRepository = mechanicRepository;
        this.mechanicMapper = mechanicMapper;
    }

    @Transactional(readOnly = true)
    public List<MechanicDTO> findAllMechanicDTO() {
        return findAllMechanic().stream().map(mechanicMapper::mechanicToMechanicDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Mechanic> findAllMechanic() {
        return mechanicRepository.findAll();
    }

    @Transactional
    public void addMechanic(String name, String surname, Double experienceYears) {
        logger.info("Обработка добавления нового механика");
        Mechanic mechanic = new Mechanic(name, surname, experienceYears);
        mechanicRepository.save(mechanic);
        logger.info("Добавлен новый механик {}", mechanic);
    }

    @Transactional
    public void removeMechanicById(Integer id) {
        logger.info("Обработка удаления механика № {}", id);
        Mechanic mechanic = mechanicRepository.findById(id).orElse(null);
        mechanicRepository.delete(mechanic);
        logger.info("Механик № {} удален", id);
    }

    @Transactional(readOnly = true)
    public Mechanic findMechanicById(Integer id) {
        return mechanicRepository.findById(id).orElseThrow(() -> new MechanicException(
                "Механик с id - " + id + " не существует"
        ));
    }

    @Transactional(readOnly = true)
    public List<MechanicDTO> showSortedMechanicByAlphabet(Boolean flag) {
        logger.info("Обработка сортировки механиков по алфавиту");
        List<MechanicDTO> sortedList = mechanicRepository.sortBy(MechanicSortType.ALPHABET.getDisplayName(), flag)
                .stream().map(mechanicMapper::mechanicToMechanicDTO).collect(Collectors.toList());
        logger.info("Механики отсортированы по алфавиту");
        return sortedList;
    }

    @Transactional(readOnly = true)
    public List<MechanicDTO> showSortedMechanicByBusy() {
        logger.info("Обработка сортировки механиков по занятости");
        List<MechanicDTO> sortedList = mechanicRepository.sortBy(MechanicSortType.BUSY.getDisplayName(), true)
                .stream().map(mechanicMapper::mechanicToMechanicDTO).collect(Collectors.toList());
        logger.info("Механики отсортированы по занятости");
        return sortedList;
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

    @Transactional
    public void importFromCSV(String resourceName) {
        logger.info("Обработка импорта данных механиков из файла {}", resourceName);
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
                    if (!isMechanicExists(id)) {
                        mechanicRepository.save(mechanic);
                    } else {
                        mechanicRepository.update(mechanic);
                    }
                }
            }

            logger.info("Данные успешно импортированы из файла {}", resourceName);
        } catch (IOException e) {
            logger.error("Ошибка при импорте данных механиков из файла {}", resourceName);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String exportToCSV() {
        logger.info("Обработка экспорта данных механиков в файл");
        List<Mechanic> mechanicList = findAllMechanic();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id;name;surname;experience_years;is_busy").append(System.lineSeparator());
        mechanicList.forEach(mechanic -> stringBuilder.append(String.format("%d;%s;%s;%.2f;%s",
                        mechanic.getId(),
                        mechanic.getName(),
                        mechanic.getSurname(),
                        mechanic.getExperienceYears(),
                        mechanic.getIsBusy()))
                .append(System.lineSeparator()));
        logger.info("Данные успешно записаны в файл");
        return stringBuilder.toString();
    }

    @Transactional
    public void updateMechanic(Mechanic mechanic) {
        mechanicRepository.update(mechanic);
    }

    @Transactional(readOnly = true)
    public boolean isMechanicExists(Integer id) {
        return mechanicRepository.checkIsMechanicExists(id);
    }
}
