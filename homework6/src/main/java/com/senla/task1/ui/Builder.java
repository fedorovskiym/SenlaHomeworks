package com.senla.task1.ui;

import com.senla.task1.annotations.Inject;
import com.senla.task1.factory.ApplicationContext;
import java.util.Arrays;

public class Builder {

    private Menu rootMenu;
    @Inject
    private ActionFactory actionFactory;
    @Inject
    private ApplicationContext applicationContext;

    public void build() {
        rootMenu = new Menu("Главное меню", null);

        Menu mechanicsMenu = new Menu("Механики", Arrays.asList(
                createMenuItem("Добавить механика", actionFactory.addMechanicAction()),
                createMenuItem("Показать всех механиков", actionFactory.showMechanicsAction()),
                createMenuItem("Удалить механика", actionFactory.removeMechanicById()),
                createMenuItem("Отсортировать механиков по алфавиту", actionFactory.showSortedMechanicsByAlphabet()),
                createMenuItem("Отсортировать механиков по занятости", actionFactory.showSortedMechanicsByBusy()),
                createMenuItem("Импорт механиков из файла", actionFactory.importMechanicsFromFileAction()),
                createMenuItem("Экспорт механиков в файл", actionFactory.exportMechanicToFileAction()),
                createMenuItem("Назад", actionFactory.goBackAction(), rootMenu)
        ));
        mechanicsMenu.setParentMenu(rootMenu);

        Menu garageMenu = new Menu("Гараж", Arrays.asList(
                createMenuItem("Добавить место", actionFactory.addGaragePlaceAction()),
                createMenuItem("Показать свободные места", actionFactory.showGaragePlacesAction()),
                createMenuItem("Удалить место в гараже", actionFactory.removeGaragePlace()),
                createMenuItem("Импорт гаражных мест из файла", actionFactory.importGaragePlaceFromFileAction()),
                createMenuItem("Экспорт гаражных мест в файл", actionFactory.exportGaragePlaceToFileAction()),
                createMenuItem("Назад", actionFactory.goBackAction(), rootMenu)
        ));
        garageMenu.setParentMenu(rootMenu);

        Menu ordersMenu = new Menu("Заказы", Arrays.asList(
                createMenuItem("Создать заказ", actionFactory.createOrderAction()),
                createMenuItem("Принять заказ", actionFactory.acceptOrderAction()),
                createMenuItem("Закрыть заказ", actionFactory.closeOrderAction()),
                createMenuItem("Отменить заказ", actionFactory.cancelOrderAction()),
                createMenuItem("Удалить заказ", actionFactory.deleteOrderAction()),
                createMenuItem("Сместить время выполнения заказов", actionFactory.shiftOrderTimeAction()),
                createMenuItem("Найти заказ по номеру механика", actionFactory.findOrderByMechanicIdAction()),
                createMenuItem("Показать заказы по статусу", actionFactory.showOrdersByStatusAction()),
                createMenuItem("Показать все заказы", actionFactory.showAllOrdersAction()),
                createMenuItem("Назад", actionFactory.goBackAction(), rootMenu)
        ));
        ordersMenu.setParentMenu(rootMenu);

        rootMenu.setMenuItems(Arrays.asList(
                createMenuItem("Механики", mechanicsMenu),
                createMenuItem("Гараж", garageMenu),
                createMenuItem("Заказы", ordersMenu)
        ));
    }

    private MenuItem createMenuItem(String title, IAction action) {
        MenuItem item = new MenuItem(title);
        item.setAction(action);
        return item;
    }

    private MenuItem createMenuItem(String title, IAction action, Menu nextMenu) {
        MenuItem item = new MenuItem(title);
        item.setAction(action);
        item.setNextMenu(nextMenu);
        return item;
    }

    private MenuItem createMenuItem(String title, Menu nextMenu) {
        MenuItem item = new MenuItem(title);
        item.setNextMenu(nextMenu);
        return item;
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
