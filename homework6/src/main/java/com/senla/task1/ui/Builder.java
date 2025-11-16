package com.senla.task1.ui;

import java.util.Arrays;

public class Builder {

    private Menu rootMenu;

    public void build(ActionFactory factory) {

        rootMenu = new Menu("Главное меню", null, null);

        Menu mechanicsMenu = new Menu("Механики", Arrays.asList(
                new MenuItem("Добавить механика", factory.addMechanicAction(), null),
                new MenuItem("Показать всех механиков", factory.showMechanicsAction(), null),
                new MenuItem("Удалить механика", factory.removeMechanicById(), null),
                new MenuItem("Отсортировать механиков по алфавиту", factory.showSortedMechanicsByAlphabet(), null),
                new MenuItem("Отсортировать механиков по занятости", factory.showSortedMechanicsByBusy(), null),
                new MenuItem("Импорт механиков из файла", factory.importMechanicsFromFileAction(), null),
                new MenuItem("Экспорт механиков в файл", factory.exportMechanicToFileAction(), null),
                new MenuItem("Назад", factory.goBackAction(), rootMenu)
        ), rootMenu);

        Menu garageMenu = new Menu("Гараж", Arrays.asList(
                new MenuItem("Добавить место", factory.addGaragePlaceAction(), null),
                new MenuItem("Показать свободные места", factory.showGaragePlacesAction(), null),
                new MenuItem("Удалить место в гараже", factory.removeGaragePlace(), null),
                new MenuItem("Импорт гаражных мест из файла", factory.importGaragePlaceFromFileAction(), null),
                new MenuItem("Экспорт гаражных мест в файл", factory.exportGaragePlaceToFileAction(), null),
                new MenuItem("Назад", factory.goBackAction(), rootMenu)
        ), rootMenu);

        Menu ordersMenu = new Menu("Заказы", Arrays.asList(
                new MenuItem("Создать заказ", factory.createOrderAction(), null),
                new MenuItem("Принять заказ", factory.acceptOrderAction(), null),
                new MenuItem("Закрыть заказ", factory.closeOrderAction(), null),
                new MenuItem("Отменить заказ", factory.cancelOrderAction(), null),
                new MenuItem("Удалить заказ", factory.deleteOrderAction(), null),
                new MenuItem("Сместить время выполнения заказов", factory.shiftOrderTimeAction(), null),
                new MenuItem("Найти заказ по номеру механика", factory.findOrderByMechanicIdAction(), null),
                new MenuItem("Показать заказы по статусу", factory.showOrdersByStatusAction(), null),
                new MenuItem("Показать все заказы", factory.showAllOrdersAction(), null),
                new MenuItem("Отсортировать заказы по дате подачи", factory.showSortedOrdersByDateOfSubmissionAction(), null),
                new MenuItem("Отсортировать заказы по дате выполнения", factory.showSortedOrdersByDateOfCompletionAction(), null),
                new MenuItem("Отсортировать заказы по цене", factory.showSortedOrdersByPriceAction(), null),
                new MenuItem("Показать заказы за период времени", factory.showOrdersOverPeriodOfTimeAction(), null),
                new MenuItem("Показать ближайшую доступную дату", factory.showNearestAvailableSlot(), null),
                new MenuItem("Показать свободные места на определенную дату", factory.getAvailableSlot(), null),
                new MenuItem("Импорт заказов из файла", factory.importOrderFromFileAction(), null),
                new MenuItem("Экспорт заказов в файл", factory.exportOrderToFileAction(), null),
                new MenuItem("Назад", factory.goBackAction(), rootMenu)
        ), rootMenu);

        rootMenu.setMenuItems(Arrays.asList(
                new MenuItem("Механики", null, mechanicsMenu),
                new MenuItem("Гараж", null, garageMenu),
                new MenuItem("Заказы", null, ordersMenu)
        ));
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
