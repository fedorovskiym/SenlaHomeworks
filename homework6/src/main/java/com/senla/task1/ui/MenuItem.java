package com.senla.task1.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuItem {

    private String title;
    @Autowired
    private IAction action;
    @Autowired
    private Menu nextMenu;

    public MenuItem() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public Menu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(Menu nextMenu) {
        this.nextMenu = nextMenu;
    }

    public void doAction() {
        if (action != null) {
            action.execute();
        }
    }
}