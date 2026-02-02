package com.senla.task1.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Menu {

    private String name;
    private List<MenuItem> menuItems;
    @Autowired
    private Menu parentMenu;

    public Menu() {
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