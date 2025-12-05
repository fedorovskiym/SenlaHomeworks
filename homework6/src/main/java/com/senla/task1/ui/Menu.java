package com.senla.task1.ui;

import com.senla.task1.annotations.Inject;
import java.util.List;

public class Menu {

    private String name;
    private List<MenuItem> menuItems;
    @Inject
    private Menu parentMenu;

    public Menu(String name, List<MenuItem> menuItems) {
        this.name = name;
        this.menuItems = menuItems;
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
