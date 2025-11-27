package com.senla.task1.ui;

import java.util.Scanner;

public class MenuController {

    private static MenuController instance;
    private final Builder builder = new Builder();
    private Navigator navigator;

    public MenuController() {
    }

    public static MenuController instance() {
        if (instance == null) {
            instance = new MenuController();
        }
        return instance;
    }

    public void init(ActionFactory actionFactory) {
        builder.build(actionFactory);
        navigator = new Navigator(builder.getRootMenu());
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                navigator.printMenu();
                int action = Integer.parseInt(scanner.nextLine());
                navigator.navigate(action);
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод");
            }
        }
    }
}
