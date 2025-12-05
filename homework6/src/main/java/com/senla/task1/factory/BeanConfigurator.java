package com.senla.task1.factory;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;

public class BeanConfigurator {

    public <T> Class<? extends T> getImplClass(Class<T> interfaceClass) {
        try {
            String packageName = interfaceClass.getPackageName();
            String path = packageName.replace('.', '/');

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(path);

            if (url == null) {
                throw new RuntimeException("Пакет не найден: " + packageName);
            }

            File directory = new File(url.toURI());
            File[] files = directory.listFiles();
            if (files == null) {
                throw new RuntimeException("Пакет пустой: " + packageName);
            }

            for (File file : files) {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }

                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> aClass = Class.forName(className);

                if (interfaceClass.isAssignableFrom(aClass) && !aClass.isInterface() && !Modifier.isAbstract(aClass.getModifiers())) {
                    return (Class<? extends T>) aClass;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске реализации интерфейса: " + e.getMessage(), e);
        }

        throw new RuntimeException("Реализация интерфейса не найдена для " + interfaceClass.getName());
    }
}

