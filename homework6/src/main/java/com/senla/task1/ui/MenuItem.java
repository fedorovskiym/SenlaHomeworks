package com.senla.task1.ui;

import com.senla.task1.annotations.Inject;

public class MenuItem {

    private String title;
    @Inject
    private IAction action;
    @Inject
    private Menu nextMenu;

    public MenuItem(String title) {
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
