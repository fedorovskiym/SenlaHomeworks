package com.senla.task1;

import com.senla.task1.config.SpringConfig;
import com.senla.task1.ui.MenuController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AutoServiceMain {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class)){
            MenuController menuController = context.getBean(MenuController.class);
            menuController.init();
            menuController.run();
        }
    }
}
