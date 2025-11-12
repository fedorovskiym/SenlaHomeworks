package com.senla.task1.ui;

import com.senla.task1.controller.AutoServiceController;
import com.senla.task1.controller.GaragePlaceController;
import com.senla.task1.controller.MechanicController;
import com.senla.task1.controller.OrderController;
import com.senla.task1.models.Mechanic;

import java.util.Scanner;

public class ConsoleActionFactory implements ActionFactory {

    private final MechanicController mechanicController;
    private final GaragePlaceController garagePlaceController;
    private final OrderController orderController;
    private final AutoServiceController autoServiceController;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleActionFactory(MechanicController mechanicController, GaragePlaceController garagePlaceController, OrderController orderController, AutoServiceController autoServiceController) {
        this.mechanicController = mechanicController;
        this.garagePlaceController = garagePlaceController;
        this.orderController = orderController;
        this.autoServiceController = autoServiceController;
    }

    @Override
    public IAction addMechanicAction() {
        return () -> {
            System.out.println("Введите имя механика: ");
            String name = scanner.nextLine();
            System.out.println("Введите фамилию механика: ");
            String surname = scanner.nextLine();
            System.out.println("Введите опыт механика в годах: ");
            double experienceYears = scanner.nextDouble();
            scanner.nextLine();
            System.out.println();
            if (name.isEmpty() || surname.isEmpty()) {
                System.out.println("Ошибка ввода. Механик не добавлен");
                return;
            }
            mechanicController.addMechanic(new Mechanic(name, surname, experienceYears));
            System.out.println();
        };
    }

    @Override
    public IAction removeMechanicById() {
        return () -> {
            System.out.println("Введите номер механика, которого хотите удалить: ");
            int id = scanner.nextInt();
            System.out.println();
            mechanicController.removeMechanicById(id);
            System.out.println();
        };
    }

    @Override
    public IAction showMechanicsAction() {
        return mechanicController::showAllMechanics;
    }

    @Override
    public IAction showSortedMechanicsByAlphabet() {
        return () -> {
            System.out.println("""
                    1 - Отобразить механиков в обычном порядке (А - Я)
                    2 - Отобразить механиков в обратном порядке
                    """);
            int flag = scanner.nextInt();
            System.out.println();
            switch (flag) {
                case 1:
                    mechanicController.showSortedMechanicByAlphabet(true);
                    break;
                case 2:
                    mechanicController.showSortedMechanicByAlphabet(false);
                    break;
                default:
                    System.out.println("Неверный выбор");
            }
        };
    }

    @Override
    public IAction showSortedMechanicsByBusy() {
        return mechanicController::showSortedMechanicByBusy;
    }

    @Override
    public IAction addGaragePlaceAction() {
        return () -> {
            System.out.println("Введите номер места: ");
            int placeNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            garagePlaceController.addGaragePlace(placeNumber);
            System.out.println();
        };
    }

    @Override
    public IAction showGaragePlacesAction() {
        return garagePlaceController::showFreeGaragePlaces;
    }

    @Override
    public IAction removeGaragePlace() {
        return () -> {
            System.out.println("Введите номер гаража, который хотите удалить: ");
            int placeNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            garagePlaceController.removeGaragePlace(placeNumber);
            System.out.println();
        };
    }

    @Override
    public IAction createOrderAction() {
        return () -> {
            System.out.println("Механики: ");
            mechanicController.showAllMechanics();
            System.out.println("Свободные места в гараже: ");
            garagePlaceController.showFreeGaragePlaces();
            System.out.println("Введите название машины: ");
            String carModel = scanner.nextLine();
            System.out.println("Введите номер механика, который будет выполнять заказ: ");
            int mechanicId = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите номер места в гараже: ");
            int placeNumber = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите цену: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.println("Введите продолжительность (в часах): ");
            int hours = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите продолжительность (в минутах): ");
            int minutes = Integer.parseInt(scanner.nextLine());
            autoServiceController.createOrder(carModel, mechanicId, placeNumber, price, hours, minutes);
            System.out.println();
        };
    }

    @Override
    public IAction getAvailableSlot() {
        return () -> {
            System.out.println("Введите год: ");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите месяц: ");
            int month = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите день: ");
            int day = Integer.parseInt(scanner.nextLine());
            autoServiceController.getAvailableSlot(year, month, day);
        };
    }

    @Override
    public IAction acceptOrderAction() {
        return () -> {
            System.out.println("Введите номер заказа: ");
            int id = Integer.parseInt(scanner.nextLine());
            orderController.acceptOrder(id);
        };
    }

    @Override
    public IAction closeOrderAction() {
        return () -> {
            System.out.println("Введите номер заказа: ");
            int id = Integer.parseInt(scanner.nextLine());
            orderController.closeOrder(id);
        };
    }

    @Override
    public IAction cancelOrderAction() {
        return () -> {
            System.out.println("Введите номер заказа: ");
            int id = Integer.parseInt(scanner.nextLine());
            orderController.cancelOrder(id);
        };
    }

    @Override
    public IAction shiftOrderTimeAction() {
        return () -> {
            System.out.println("Введите часы: ");
            int hours = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите минуты: ");
            int minutes = Integer.parseInt(scanner.nextLine());
            orderController.shiftOrdersTime(hours, minutes);
        };
    }

    @Override
    public IAction deleteOrderAction() {
        return () -> {
            System.out.println("Введите номер заказа: ");
            int id = Integer.parseInt(scanner.nextLine());
            orderController.deleteOrder(id);
        };
    }

    @Override
    public IAction findOrderByMechanicIdAction() {
        return () -> {
            System.out.println("Введите номер механика: ");
            int mechanicId = Integer.parseInt(scanner.nextLine());
            orderController.findOrderByMechanicId(mechanicId);
        };
    }

    @Override
    public IAction showOrdersByStatusAction() {
        return () -> {
            System.out.println("Выберите, по какому статусу отобразить заказ: ");
            System.out.println("""
                    1 - Ожидает
                    2 - Принят
                    3 - Выполнен
                    4 - Отменен
                    5 - Удален
                    """);
            int status = Integer.parseInt(scanner.nextLine());
            switch (status) {
                case 1:
                    orderController.showOrdersByStatus("Ожидает");
                    break;
                case 2:
                    orderController.showOrdersByStatus("Принят");
                    break;
                case 3:
                    orderController.showOrdersByStatus("Выполнен");
                    break;
                case 4:
                    orderController.showOrdersByStatus("Отменен");
                    break;
                case 5:
                    orderController.showOrdersByStatus("Удален");
                    break;
                default:
                    System.out.println("Неверный статус");
                    break;
            }
        };
    }

    @Override
    public IAction showAllOrdersAction() {
        return orderController::showAllOrders;
    }

    @Override
    public IAction showSortedOrdersByDateOfSubmissionAction() {
        return () -> {
            System.out.println("""
                    1 - Отобразить в обычном порядке
                    2 - Отобразить в обратном порядке
                    """);
            int flag = Integer.parseInt(scanner.nextLine());
            switch (flag) {
                case 1:
                    orderController.showSortedOrdersByDateOfSubmission(true);
                    break;
                case 2:
                    orderController.showSortedOrdersByDateOfSubmission(false);
                    break;
                default:
                    System.out.println("Неверный выбор");
                    break;
            }
        };
    }

    @Override
    public IAction showSortedOrdersByDateOfCompletionAction() {
        return () -> {
            System.out.println("""
                    1 - Отобразить в обычном порядке
                    2 - Отобразить в обратном порядке
                    """);
            int flag = Integer.parseInt(scanner.nextLine());
            switch (flag) {
                case 1:
                    orderController.showSortedOrdersByDateOfCompletion(true);
                    break;
                case 2:
                    orderController.showSortedOrdersByDateOfCompletion(false);
                    break;
                default:
                    System.out.println("Неверный выбор");
                    break;
            }
        };
    }

    @Override
    public IAction showSortedOrdersByPriceAction() {
        return () -> {
            System.out.println("""
                    1 - Отобразить в обычном порядке
                    2 - Отобразить в обратном порядке
                    """);
            int flag = Integer.parseInt(scanner.nextLine());
            switch (flag) {
                case 1:
                    orderController.showSortedOrdersByPrice(true);
                    break;
                case 2:
                    orderController.showSortedOrdersByPrice(false);
                    break;
                default:
                    System.out.println("Неверный выбор");
                    break;
            }
        };
    }

    @Override
    public IAction showOrdersOverPeriodOfTimeAction() {
        return () -> {
            System.out.println("С какого года: ");
            int fromYear = Integer.parseInt(scanner.nextLine());
            System.out.println("С какого месяца: ");
            int fromMonth = Integer.parseInt(scanner.nextLine());
            System.out.println("С какого дня: ");
            int fromDay = Integer.parseInt(scanner.nextLine());
            System.out.println("По какой год: ");
            int toYear = Integer.parseInt(scanner.nextLine());
            System.out.println("По какой месяц: ");
            int toMonth = Integer.parseInt(scanner.nextLine());
            System.out.println("По какой день: ");
            int toDay = Integer.parseInt(scanner.nextLine());
            if ((fromMonth < 0 || fromMonth > 12) || (toMonth < 0 || toMonth > 12) || (fromDay < 0 || fromDay > 31) || (toDay < 0 || toDay > 31)) {
                System.out.println("Неверный ввод");
                return;
            }

            System.out.println("""
                    1 - Сортировать по дате подачи
                    2 - Сортировать по дате выполнения
                    3 - Сортировать по цене
                    """);
            String sortType;
            int userSortType = Integer.parseInt(scanner.nextLine());

            switch (userSortType) {
                case 1:
                    sortType = "submission";
                    break;
                case 2:
                    sortType = "completion";
                    break;
                case 3:
                    sortType = "price";
                    break;
                default:
                    System.out.println("Неверный выбор");
                    return;
            }

            System.out.println("""
                    1 - В обычном порядке
                    2 - В обратном порядке
                    """);
            int flag = Integer.parseInt(scanner.nextLine());

            switch (flag) {
                case 1:
                    orderController.showOrdersOverPeriodOfTime(fromYear, fromMonth, fromDay, toYear, toMonth, toDay, sortType, true);
                    break;
                case 2:
                    orderController.showOrdersOverPeriodOfTime(fromYear, fromMonth, fromDay, toYear, toMonth, toDay, sortType, false);
                    break;
                default:
                    System.out.println("Неверный выбор");
                    break;
            }
        };
    }

    @Override
    public IAction showNearestAvailableSlot() {
        return orderController::showNearestAvailableSlot;
    }

    @Override
    public IAction goBackAction() {
        return () -> {
        };
    }
}
