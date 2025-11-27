package com.senla.task1;

import com.senla.task1.config.AutoServiceConfig;
import com.senla.task1.controller.AutoServiceController;
import com.senla.task1.controller.GaragePlaceController;
import com.senla.task1.controller.MechanicController;
import com.senla.task1.controller.OrderController;
import com.senla.task1.models.Mechanic;
import com.senla.task1.service.AutoService;
import com.senla.task1.ui.ConsoleActionFactory;
import com.senla.task1.ui.MenuController;

import java.time.Duration;
import java.time.LocalDateTime;

public class AutoServiceMain {
    public static void main(String[] args) {

        MechanicController mechanicController = new MechanicController();
        GaragePlaceController garagePlaceController = new GaragePlaceController();
        OrderController orderController = new OrderController();
        AutoServiceController autoServiceController = new AutoServiceController();
        AutoServiceConfig autoServiceConfig = new AutoServiceConfig();

        ConsoleActionFactory consoleActionFactory = new ConsoleActionFactory(mechanicController, garagePlaceController, orderController, autoServiceController, autoServiceConfig);

        MenuController controller = MenuController.instance();
        controller.init(consoleActionFactory);
        controller.run();

    }
}
