package com.senla.task1.ui;

import com.senla.task1.annotations.Inject;
import com.senla.task1.config.AutoServiceConfig;
import com.senla.task1.controller.AutoServiceController;
import com.senla.task1.controller.GaragePlaceController;
import com.senla.task1.controller.MechanicController;
import com.senla.task1.controller.OrderController;
import com.senla.task1.exceptions.ExceptionHandler;
import com.senla.task1.exceptions.InvalidInputException;
import com.senla.task1.models.enums.OrderSortType;
import com.senla.task1.models.enums.OrderStatus;

import java.util.Scanner;

public class ConsoleActionFactory implements ActionFactory {

    private final MechanicController mechanicController;
    private final GaragePlaceController garagePlaceController;
    private final OrderController orderController;
    private final AutoServiceController autoServiceController;
    private final AutoServiceConfig autoServiceConfig;
    private final Scanner scanner = new Scanner(System.in);

    @Inject
    public ConsoleActionFactory(MechanicController mechanicController, GaragePlaceController garagePlaceController, OrderController orderController, AutoServiceController autoServiceController, AutoServiceConfig autoServiceConfig) {
        this.mechanicController = mechanicController;
        this.garagePlaceController = garagePlaceController;
        this.orderController = orderController;
        this.autoServiceController = autoServiceController;
        this.autoServiceConfig = autoServiceConfig;
    }

    @Override
    public IAction addMechanicAction() {
        return () -> {
            try {
                System.out.println("Введите имя механика: ");
                String name = scanner.nextLine();
                System.out.println("Введите фамилию механика: ");
                String surname = scanner.nextLine();
                System.out.println("Введите опыт механика в годах: ");
                double experienceYears = Double.parseDouble(scanner.nextLine());
                mechanicController.addMechanic(name, surname, experienceYears);
                System.out.println();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }


    @Override
    public IAction removeMechanicById() {
        return () -> {
            try {
                System.out.println("Введите ID механика: ");
                int id = Integer.parseInt(scanner.nextLine());
                mechanicController.removeMechanicById(id);
                System.out.println();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }


    @Override
    public IAction showMechanicsAction() {
        return mechanicController::showAllMechanics;
    }

    @Override
    public IAction showSortedMechanicsByAlphabet() {
        return () -> {
            try {
                System.out.println("""
                        1 - Отобразить в порядке А–Я
                        2 - Отобразить в порядке Я–А
                        """);
                int flag = Integer.parseInt(scanner.nextLine());
                switch (flag) {
                    case 1:
                        mechanicController.showSortedMechanicByAlphabet(true);
                        break;
                    case 2:
                        mechanicController.showSortedMechanicByAlphabet(false);
                        break;
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }


    @Override
    public IAction showSortedMechanicsByBusy() {
        return mechanicController::showSortedMechanicByBusy;
    }

    @Override
    public IAction importMechanicsFromFileAction() {
        return () -> {
            try {
                System.out.println("Введите название файла: ");
                String fileName = scanner.nextLine();
                mechanicController.importMechanicFromCSV(fileName);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction exportMechanicToFileAction() {
        return () -> {
            try {
                System.out.println("Введите путь: ");
                String filePath = scanner.nextLine();
                mechanicController.exportMechanicToCSV(filePath);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction addGaragePlaceAction() {
        return () -> {
            if (!autoServiceConfig.isAllowAddGaragePlace()) {
                System.out.println("Добавление места в гараже отключено");
                return;
            }
            try {
                System.out.println("Введите номер места: ");
                int placeNumber = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                garagePlaceController.addGaragePlace(placeNumber);
                System.out.println();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction showGaragePlacesAction() {
        return garagePlaceController::showFreeGaragePlaces;
    }

    @Override
    public IAction removeGaragePlace() {
        return () -> {
            if (!autoServiceConfig.isAllowDeleteGaragePlace()) {
                System.out.println("Удаление места в гараже отключено");
            }
            try {
                System.out.println("Введите номер гаража, который хотите удалить: ");
                int placeNumber = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                garagePlaceController.removeGaragePlace(placeNumber);
                System.out.println();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction importGaragePlaceFromFileAction() {
        return () -> {
            try {
                System.out.println("Введите название файла: ");
                String filePath = scanner.nextLine();
                garagePlaceController.importGaragePlaceFromCSV(filePath);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction exportGaragePlaceToFileAction() {
        return () -> {
            try {
                System.out.println("Введите путь: ");
                String filePath = scanner.nextLine();
                garagePlaceController.exportGaragePlaceToCSV(filePath);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction createOrderAction() {
        return () -> {
            try {
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
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction getAvailableSlot() {
        return () -> {
            try {
                System.out.println("Введите год: ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.println("Введите месяц: ");
                int month = Integer.parseInt(scanner.nextLine());
                System.out.println("Введите день: ");
                int day = Integer.parseInt(scanner.nextLine());
                autoServiceController.getAvailableSlot(year, month, day);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction acceptOrderAction() {
        return () -> {
            try {
                System.out.println("Введите номер заказа: ");
                int id = Integer.parseInt(scanner.nextLine());
                orderController.acceptOrder(id);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction closeOrderAction() {
        return () -> {
            try {
                System.out.println("Введите номер заказа: ");
                int id = Integer.parseInt(scanner.nextLine());
                orderController.closeOrder(id);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction cancelOrderAction() {
        return () -> {
            try {
                System.out.println("Введите номер заказа: ");
                int id = Integer.parseInt(scanner.nextLine());
                orderController.cancelOrder(id);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction shiftOrderTimeAction() {
        return () -> {
            if (!autoServiceConfig.isAllowShiftOrdersTime()) {
                System.out.println("Смещение времени заказов отключено");
                return;
            }
            try {
                System.out.println("Введите часы: ");
                int hours = Integer.parseInt(scanner.nextLine());
                System.out.println("Введите минуты: ");
                int minutes = Integer.parseInt(scanner.nextLine());
                orderController.shiftOrdersTime(hours, minutes);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction deleteOrderAction() {
        return () -> {
            if (!autoServiceConfig.isAllowDeleteOrder()) {
                System.out.println("Удаление заказа отключено");
                return;
            }
            try {
                System.out.println("Введите номер заказа: ");
                int id = Integer.parseInt(scanner.nextLine());
                orderController.deleteOrder(id);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction findOrderByMechanicIdAction() {
        return () -> {
            try {
                System.out.println("Введите номер механика: ");
                int mechanicId = Integer.parseInt(scanner.nextLine());
                orderController.findOrderByMechanicId(mechanicId);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction showOrdersByStatusAction() {
        return () -> {
            try {
                System.out.println("Выберите, по какому статусу отобразить заказ: ");
                System.out.println("""
                        1 - Ожидает
                        2 - Принят
                        3 - Выполнен
                        4 - Отменен
                        """);
                int status = Integer.parseInt(scanner.nextLine());
                switch (status) {
                    case 1:
                        orderController.showOrdersByStatus(OrderStatus.WAITING);
                        break;
                    case 2:
                        orderController.showOrdersByStatus(OrderStatus.ACCEPTED);
                        break;
                    case 3:
                        orderController.showOrdersByStatus(OrderStatus.DONE);
                        break;
                    case 4:
                        orderController.showOrdersByStatus(OrderStatus.CANCEL);
                        break;
                    default:
                        throw new InvalidInputException("Неожиданное значение: " + status);
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
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
            try {
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
                        throw new InvalidInputException("Неожиданное значение: " + flag);
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction showSortedOrdersByDateOfCompletionAction() {
        return () -> {
            try {
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
                        throw new InvalidInputException("Неожиданное значение: " + flag);
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction showSortedOrdersByPriceAction() {
        return () -> {
            try {
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
                        throw new InvalidInputException("Неожиданное значение: " + flag);
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction showOrdersOverPeriodOfTimeAction() {
        return () -> {
            try {
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

                System.out.println("""
                        1 - Сортировать по дате подачи
                        2 - Сортировать по дате выполнения
                        3 - Сортировать по цене
                        """);
                OrderSortType sortType;
                int userSortType = Integer.parseInt(scanner.nextLine());

                switch (userSortType) {
                    case 1:
                        sortType = OrderSortType.DATE_OF_SUBMISSION;
                        break;
                    case 2:
                        sortType = OrderSortType.DATE_OF_COMPLETION;
                        break;
                    case 3:
                        sortType = OrderSortType.PRICE;
                        break;
                    default:
                        throw new InvalidInputException("Неожиданное значение: " + userSortType);
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
                        throw new InvalidInputException("Неожиданное значение: " + flag);
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction showNearestAvailableSlot() {
        return orderController::showNearestAvailableSlot;
    }

    @Override
    public IAction importOrderFromFileAction() {
        return () -> {
            try {
                System.out.println("Введите название файла: ");
                String fileName = scanner.nextLine();
                autoServiceController.importOrdersFromCSV(fileName);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction exportOrderToFileAction() {
        return () -> {
            try {
                System.out.println("Введите путь: ");
                String filePath = scanner.nextLine();
                autoServiceController.exportOrdersToCSV(filePath);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        };
    }

    @Override
    public IAction goBackAction() {
        return () -> {
        };
    }
}
