package com.senla.task1.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Builder {

    private final Menu rootMenu;
    private final MenuItem menuItem;
    private final Menu menu;
    private final ConsoleActionFactory actionFactory;

    @Autowired
    public Builder(Menu rootMenu, MenuItem menuItem, Menu menu, ConsoleActionFactory actionFactory) {
        this.rootMenu = rootMenu;
        this.menuItem = menuItem;
        this.menu = menu;
        this.actionFactory = actionFactory;
    }

    public void build() {
        rootMenu.setName("Главное меню");

        Menu mechanicsMenu = cloneMenu();
        mechanicsMenu.setName("Механики");
        mechanicsMenu.setParentMenu(rootMenu);
        mechanicsMenu.setMenuItems(Arrays.asList(
                createMenuItem("Добавить механика", actionFactory.addMechanicAction()),
                createMenuItem("Показать всех механиков", actionFactory.showMechanicsAction()),
                createMenuItem("Удалить механика", actionFactory.removeMechanicById()),
                createMenuItem("Отсортировать механиков по алфавиту", actionFactory.showSortedMechanicsByAlphabet()),
                createMenuItem("Отсортировать механиков по занятости", actionFactory.showSortedMechanicsByBusy()),
                createMenuItem("Импорт механиков из файла", actionFactory.importMechanicsFromFileAction()),
                createMenuItem("Экспорт механиков в файл", actionFactory.exportMechanicToFileAction()),
                createMenuItem("Назад", actionFactory.goBackAction(), rootMenu)
        ));

        Menu garageMenu = cloneMenu();
        garageMenu.setName("Гараж");
        garageMenu.setParentMenu(rootMenu);
        garageMenu.setMenuItems(Arrays.asList(
                createMenuItem("Добавить место", actionFactory.addGaragePlaceAction()),
                createMenuItem("Показать свободные места", actionFactory.showGaragePlacesAction()),
                createMenuItem("Удалить место в гараже", actionFactory.removeGaragePlace()),
                createMenuItem("Импорт гаражных мест из файла", actionFactory.importGaragePlaceFromFileAction()),
                createMenuItem("Экспорт гаражных мест в файл", actionFactory.exportGaragePlaceToFileAction()),
                createMenuItem("Назад", actionFactory.goBackAction(), rootMenu)
        ));

        Menu ordersMenu = cloneMenu();
        ordersMenu.setName("Заказы");
        ordersMenu.setParentMenu(rootMenu);
        ordersMenu.setMenuItems(Arrays.asList(
                createMenuItem("Создать заказ", actionFactory.createOrderAction()),
                createMenuItem("Принять заказ", actionFactory.acceptOrderAction()),
                createMenuItem("Закрыть заказ", actionFactory.closeOrderAction()),
                createMenuItem("Отменить заказ", actionFactory.cancelOrderAction()),
                createMenuItem("Удалить заказ", actionFactory.deleteOrderAction()),
                createMenuItem("Сместить время выполнения заказов", actionFactory.shiftOrderTimeAction()),
                createMenuItem("Найти заказ по номеру механика", actionFactory.findOrderByMechanicIdAction()),
                createMenuItem("Показать заказы по статусу", actionFactory.showOrdersByStatusAction()),
                createMenuItem("Показать все заказы", actionFactory.showAllOrdersAction()),
                createMenuItem("Отсортировать заказы по дате подачи", actionFactory.showSortedOrdersByDateOfSubmissionAction()),
                createMenuItem("Отсортировать заказы по дате выполнения", actionFactory.showSortedOrdersByDateOfCompletionAction()),
                createMenuItem("Отсортировать заказы по цене", actionFactory.showSortedOrdersByPriceAction()),
                createMenuItem("Показать заказы за период времени", actionFactory.showOrdersOverPeriodOfTimeAction()),
                createMenuItem("Показать ближайшую доступную дату", actionFactory.showNearestAvailableSlot()),
                createMenuItem("Показать свободные места на определенную дату", actionFactory.getAvailableSlot()),
                createMenuItem("Импорт заказов из файла", actionFactory.importOrderFromFileAction()),
                createMenuItem("Экспорт заказов в файл", actionFactory.exportOrderToFileAction()),
                createMenuItem("Назад", actionFactory.goBackAction(), rootMenu)
        ));

        rootMenu.setMenuItems(Arrays.asList(
                createMenuItem("Механики", mechanicsMenu),
                createMenuItem("Гараж", garageMenu),
                createMenuItem("Заказы", ordersMenu)
        ));
    }

    private MenuItem createMenuItem(String title, IAction action) {
        MenuItem item = cloneMenuItem();
        item.setTitle(title);
        item.setAction(action);
        return item;
    }

    private MenuItem createMenuItem(String title, IAction action, Menu nextMenu) {
        MenuItem item = cloneMenuItem();
        item.setTitle(title);
        item.setAction(action);
        item.setNextMenu(nextMenu);
        return item;
    }

    private MenuItem createMenuItem(String title, Menu nextMenu) {
        MenuItem item = cloneMenuItem();
        item.setTitle(title);
        item.setNextMenu(nextMenu);
        return item;
    }

    private MenuItem cloneMenuItem() {
        try {
            return menuItem.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать MenuItem");
        }
    }

    private Menu cloneMenu() {
        try {
            return menu.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать Menu");
        }
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
