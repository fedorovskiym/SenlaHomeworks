package com.senla.task1.service;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.dao.OrderDAOImpl;
import com.senla.task1.exceptions.OrderException;
import com.senla.task1.models.Order;
import com.senla.task1.models.enums.OrderStatus;
import com.senla.task1.models.enums.OrderSortType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderService {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final String folderPath = "data";
    private final String fileName = "order.bin";
    private final OrderDAOImpl orderDAO;
    private final static Logger logger = LogManager.getLogger(OrderService.class);

    @PostConstruct
    public void postConstruct() {
        System.out.println("Сервис заказов создался");
    }

    @Inject
    public OrderService(OrderDAOImpl orderDAO) {
        this.orderDAO = orderDAO;
        registerShutdown();
    }

    public OrderDAOImpl getOrderDAO() {
        return orderDAO;
    }

    public List<Order> findAllOrders() {
        return orderDAO.findAll();
    }

    public void addOrder(Order order) {
        LocalDateTime endTimeLastOrder = getEndDateTimeLastOrder();
        if (endTimeLastOrder != null) {
            order.setPlannedCompletionDateTime(endTimeLastOrder);
            order.setEndDateTime(endTimeLastOrder.plus(order.getDuration()));
            orderDAO.save(order);
        } else {
            acceptOrder(order.getId());
            orderDAO.save(order);
        }
        System.out.println("Заказ: " + order.getCarName() + " принят в гараж №" + order.getGaragePlace().getPlaceNumber() + ". Назначен механик " + order.getMechanic().getName());
        System.out.println("Статус заказа: '" + order.getStatus().getDisplayName() + "'");
        if (order.getPlannedCompletionDateTime() != null) {
            System.out.println("Примерное начало выполнения заказа: " + order.getPlannedCompletionDateTime().format(dateTimeFormatter));
        }
        System.out.println();
    }

    public void acceptOrder(Integer id) {
        logger.info("Обработка принятия заказа № {}", id);
        Order order = orderDAO.findOrderById(id).orElseThrow(() -> new OrderException(
                "Заказ с №" + id + " не найден"
        ));

        if (order.getStatus() == OrderStatus.ACCEPTED) {
            System.out.println("Заказ №" + id + " уже принят");
            return;
        }

        LocalDateTime lastEndDateTime = getEndDateTimeLastOrder();
        if (lastEndDateTime != null) {
            order.setCompletionDateTime(lastEndDateTime);
            order.setEndDateTime(lastEndDateTime.plus(order.getDuration()));
        } else {
            order.setCompletionDateTime(LocalDateTime.now());
            order.setEndDateTime(order.getCompletionDateTime().plus(order.getDuration()));
        }
        orderDAO.update(order);
        logger.info("Заказ № {} принят", id);
    }

    public void closeOrder(Integer id) {
        logger.info("Обработка закрытия заказа № {}", id);
        Order order = orderDAO.findOrderById(id).orElseThrow(() -> new OrderException(
                "Заказ с №" + id + " не найден"
        ));

        order.closeOrder();
        orderDAO.update(order);
        logger.info("Заказ № {} закрыт", id);
        acceptOrder(id + 1);
    }

    public void cancelOrder(Integer id) {
        logger.info("Обработка отмены заказа № {}", id);
        Order order = orderDAO.findOrderById(id).orElseThrow(() -> new OrderException(
                "Заказ с №" + id + " не найден"
        ));

        order.cancelOrder();
        orderDAO.update(order);
        logger.info("Заказ № {} отменен", id);
    }

    public void shiftOrdersTime(Integer hours, Integer minutes) {
        logger.info("Обработка сдвига времени выполнения заказов на {} часов, {} минут", hours, minutes);
        Duration time = Duration.ofHours(hours).plusMinutes(minutes);
        List<Order> orderList = findAllOrders();
        orderList.forEach(order -> order.shiftTime(time));
        orderList.forEach(order -> orderDAO.update(order));
        logger.info("Изменено время выполнения всех заказов на {} часов, {} минут", hours, minutes);
    }


    public void deleteOrder(Integer id) {
        logger.info("Обработка удаления заказа № {}", id);
        orderDAO.delete(id);
        logger.info("Заказ № {} удален", id);
    }

    // Вывод заказа по айдишнку механика
    public void findOrderByMechanicId(Integer mechanicId) {
        logger.info("Обработка поиска заказов по механику № {}", mechanicId);
        List<Order> mechanicOrders = orderDAO.findOrderByMechanicId(mechanicId);

        if (!mechanicOrders.isEmpty()) {
            showOrders(mechanicOrders);
        } else {
            System.out.println("У данного мастера в данный момент нет заказов");
        }
        logger.info("Поиск заказов по механику № {} завершен", mechanicId);
    }

    // Получение времени окончания последнего активного заказа
    public LocalDateTime getEndDateTimeLastOrder() {
        Order order = orderDAO.getEndDateTimeLastActiveOrder().orElse(null);

        if (order == null) {
            return null;
        }

        return order.getEndDateTime();
    }


    // Вывод заказов по статусу
    public void findOrderByStatus(OrderStatus status) {
        logger.info("Обработка вывода заказов по статусу {}", status);
        List<Order> ordersByStatus = orderDAO.finalOrderByStatus(status);

        if (!ordersByStatus.isEmpty()) {
            showOrders(ordersByStatus);
        } else {
            System.out.println("Заказов с таким статусом нет");
        }
        logger.info("Поиск заказов по статусу {} завершен", status);
    }

    // Вывод всех заказов
    public void showOrders(List<Order> orderList) {
        System.out.println("Заказы:");
        orderList.forEach(order -> System.out.println(formatOrderInfo(order)));
    }

    // Сортировка по дате подачи заявки (flag - определяет отображение)
    public void sortOrdersByDateOfSubmission(Boolean flag) {
        logger.info("Обработка сортировки заказов по дате подачи");
        showOrders(orderDAO.sortBy(OrderSortType.DATE_OF_SUBMISSION.getDisplayName(), flag));
        logger.info("Сортировка заказов по дате подачи завершена");
    }

    // Сортировка по дате выполнения (flag - определяет отображение), если у заказа нет даты выполнения, а только планируемая, то они становятся в конец
    public void sortOrdersByDateOfCompletion(Boolean flag) {
        logger.info("Обработка сортировки заказов по дате выполнения");
        showOrders(orderDAO.sortBy(OrderSortType.DATE_OF_COMPLETION.getDisplayName(), flag));
        logger.info("Сортировка заказов по дате выполнения завершена");
    }

    // Сортировка по цене (flag - определяет отображение)
    public void sortOrdersByPrice(Boolean flag) {
        logger.info("Обработка сортировки заказов по цене");
        showOrders(orderDAO.sortBy(OrderSortType.PRICE.getDisplayName(), flag));
        logger.info("Сортировка заказов по цене завершена");
    }

    // Метод для определения каким способом сортировать и выводить заявки за период времени
    public void findOrdersOverPeriodOfTime(Integer fromYear, Integer fromMonth, Integer fromDay, Integer toYear, Integer toMonth, Integer toDay, OrderSortType sortType, Boolean flag) {
        logger.info("Обработка поиска заказов за период времени");
        LocalDateTime startTime = LocalDateTime.of(fromYear, fromMonth, fromDay, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(toYear, toMonth, toDay, 23, 59);
        System.out.println("Заказы в период с " + startTime.format(dateTimeFormatter) + " по " + endTime.format(dateTimeFormatter));
        System.out.println();
        List<Order> sortedOrdersOverPeriod = orderDAO.findOrderOverPeriodOfTime(startTime, endTime, sortType.toString(), flag);
        showOrders(sortedOrdersOverPeriod);
        logger.info("Поиск заказов за период времени завершен");
    }

    public void showNearestAvailableDate() {
        logger.info("Обработка поиска ближайшей свободной даты");
        System.out.println("Ближайшая свободная дата: " + getEndDateTimeLastOrder().format(dateTimeFormatter));
        logger.info("Поиск ближайшей свободной даты завершен");
    }

    private String formatOrderInfo(Order order) {
        return String.format(
                """
                        Заказ №%d:
                        Статус: %s
                        Механик: %s %s
                        Название машины: %s
                        Место в гараже: %d
                        Дата подачи заявки: %s
                        %s\
                        %s\
                        %s\
                        %s\
                        Цена: %.2f руб. \n
                        """,
                order.getId(),
                order.getStatus().getDisplayName(),
                order.getMechanic().getName(),
                order.getMechanic().getSurname(),
                order.getCarName(),
                order.getGaragePlace().getPlaceNumber(),
                order.getSubmissionDateTime().format(dateTimeFormatter),

                order.getStatus().equals(OrderStatus.WAITING)
                        ? String.format("Планируемая дата выполнения заказа: %s\n",
                        order.getPlannedCompletionDateTime().format(dateTimeFormatter))
                        : "",

                order.getDuration() != null
                        ? String.format("Длительность: %d ч. %d мин.\n",
                        order.getDuration().toHours(), order.getDuration().toMinutesPart())
                        : "",

                order.getCompletionDateTime() != null
                        ? String.format("Начало выполнения заказа: %s\n",
                        order.getCompletionDateTime().format(dateTimeFormatter))
                        : "",

                order.getEndDateTime() != null
                        ? String.format("Конец выполнения: %s\n",
                        order.getEndDateTime().format(dateTimeFormatter))
                        : "",

                order.getPrice()
        );
    }

    public Order findOrderById(Integer id) {
        return orderDAO.findOrderById(id).orElseThrow(() -> new OrderException(
                "Заказ с id - " + id + " не найден"
        ));
    }

    public boolean isOrdersExists(Integer id) {
        return orderDAO.checkIsOrderExists(id);
    }

    public void update(Order order) {
        orderDAO.update(order);
    }

    public void save() {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(findAllOrders());
                System.out.println("Состояние заказов сохранено");
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
//            List<Order> loadedList = (List<Order>) ois.readObject();
//            orders.clear();
//            orders.addAll(loadedList);
//        } catch (IOException | ClassNotFoundException e) {
//            System.out.println("Ошибка при десериализации файла");
//        }
//    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }
}


