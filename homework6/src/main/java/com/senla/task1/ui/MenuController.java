package com.senla.task1.ui;

import com.senla.task1.annotations.Inject;
import java.util.Scanner;

public class MenuController {

    private final Builder builder;
    private final Navigator navigator;

    @Inject
    public MenuController(Builder builder, Navigator navigator) {
        this.builder = builder;
        this.navigator = navigator;
    }

    public void init() {
        builder.build();
        navigator.setCurrentMenu(builder.getRootMenu());
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
