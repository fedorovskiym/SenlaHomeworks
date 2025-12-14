package com.senla.task1;

import com.senla.task1.factory.ApplicationContext;
import com.senla.task1.ui.MenuController;


public class AutoServiceMain {
    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext();

        context.init(AutoServiceMain.class.getPackageName());

        MenuController menuController = context.getBean(MenuController.class);
        menuController.init();
        menuController.run();
    }
}
