package com.senla.task1.ui;

public class Navigator {
    private Menu currentMenu;

    public Navigator(Menu rootMenu) {
        this.currentMenu = rootMenu;
    }

    public void printMenu() {
        int i = 1;
        for (MenuItem item : currentMenu.getMenuItems()) {
            System.out.println(i++ + " - " + item.getTitle());
        }
        System.out.print("Выберите пункт: ");
    }

    public void navigate(int index) {
        if (index < 1 || index > currentMenu.getMenuItems().size()) {
            System.out.println("Неверный пункт");
            return;
        }

        MenuItem item = currentMenu.getMenuItems().get(index - 1);

        if (item.getAction() != null) {
            item.doAction();
        }

        if (item.getNextMenu() != null) {
            currentMenu = item.getNextMenu();
        }

        else if (item.getTitle().equalsIgnoreCase("Назад") && currentMenu.getParentMenu() != null) {
            currentMenu = currentMenu.getParentMenu();
        }
    }
}
