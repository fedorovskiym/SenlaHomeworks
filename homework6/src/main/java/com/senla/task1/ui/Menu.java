package com.senla.task1.ui;

import java.util.List;

public class Menu {

    private String name;
    private List<MenuItem> menuItems;
    private Menu parentMenu;

    public Menu(String name, List<MenuItem> menuItems, Menu parentMenu) {
        this.name = name;
        this.menuItems = menuItems;
        this.parentMenu = parentMenu;
    }

    public String getName() {
        return name;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }
}
