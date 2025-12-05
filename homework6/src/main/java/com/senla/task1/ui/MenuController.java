package com.senla.task1.ui;

import com.senla.task1.annotations.Inject;
import java.util.Scanner;

public class MenuController {

    @Inject
    private Builder builder;
    private Navigator navigator;
    @Inject
    private ActionFactory actionFactory;

    public void init() {
        builder.build();
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
