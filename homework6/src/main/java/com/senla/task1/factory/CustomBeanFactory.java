package com.senla.task1.factory;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomBeanFactory {

    private final BeanConfigurator beanConfigurator;
    private final ApplicationContext applicationContext;

    public CustomBeanFactory(ApplicationContext applicationContext) {
        this.beanConfigurator = new BeanConfigurator();
        this.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> tClass) {
        try {
            Class<? extends T> implementationClass = tClass;
            if (implementationClass.isInterface()) {
                implementationClass = beanConfigurator.getImplClass(tClass);
            }

            T bean = implementationClass.getDeclaredConstructor().newInstance();

            for (Field field : Arrays.stream(implementationClass.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(Inject.class))
                    .collect(Collectors.toList())) {

                field.setAccessible(true);
                Object dependency = applicationContext.getBean(field.getType());
                field.set(bean, dependency);
            }

            for (Method method : implementationClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.setAccessible(true);
                    method.invoke(bean);
                }
            }

            return bean;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании бина: " + tClass.getName(), e);
        }
    }
}
