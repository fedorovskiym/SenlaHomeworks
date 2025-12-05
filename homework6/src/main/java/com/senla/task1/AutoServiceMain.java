package com.senla.task1;

import com.senla.task1.config.AutoServiceConfig;
import com.senla.task1.config.Configurator;
import com.senla.task1.controller.AutoServiceController;
import com.senla.task1.controller.GaragePlaceController;
import com.senla.task1.controller.MechanicController;
import com.senla.task1.controller.OrderController;
import com.senla.task1.factory.ApplicationContext;
import com.senla.task1.factory.BeanConfigurator;
import com.senla.task1.factory.CustomBeanFactory;
import com.senla.task1.models.Mechanic;
import com.senla.task1.service.AutoService;
import com.senla.task1.ui.ConsoleActionFactory;
import com.senla.task1.ui.MenuController;

import java.time.Duration;
import java.time.LocalDateTime;

public class AutoServiceMain {

    public ApplicationContext run() {
        ApplicationContext applicationContext = new ApplicationContext();
        CustomBeanFactory customBeanFactory = new CustomBeanFactory(applicationContext);
        applicationContext.setCustomBeanFactory(customBeanFactory);
        return applicationContext;
    }

    public static void main(String[] args) {
        AutoServiceMain autoServiceMain = new AutoServiceMain();
        ApplicationContext context = autoServiceMain.run();
        MenuController controller = context.getBean(MenuController.class);
        controller.init();
        controller.run();
    }


}
