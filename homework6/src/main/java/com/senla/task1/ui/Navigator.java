package com.senla.task1.ui;

import com.senla.task1.annotations.FieldInject;

import java.util.List;

public class Navigator {

    @FieldInject
    private Menu rootMenu;

    public Navigator() {
    }

    public void setCurrentMenu(Menu menu) {
        this.rootMenu = menu;
    }

    public void printMenu() {
        List<MenuItem> items = rootMenu.getMenuItems();
        if (items == null || items.isEmpty()) {
            System.out.println("Меню пустое");
            return;
        }
        int i = 1;
        for (MenuItem item : items) {
            System.out.println(i++ + " - " + item.getTitle());
        }
        System.out.print("Выберите пункт: ");
    }

    public void navigate(int index) {
        List<MenuItem> items = rootMenu.getMenuItems();
        if (index < 1 || index > items.size()) {
            System.out.println("Неверный пункт");
            return;
        }
        MenuItem item = items.get(index - 1);

        if (item.getTitle().equalsIgnoreCase("Назад") && rootMenu.getParentMenu() != null) {
            rootMenu = rootMenu.getParentMenu();
            return;
        }

        if (item.getAction() != null) {
            item.doAction();
        }
        if (item.getNextMenu() != null) {
            rootMenu = item.getNextMenu();
        }
    }

}
