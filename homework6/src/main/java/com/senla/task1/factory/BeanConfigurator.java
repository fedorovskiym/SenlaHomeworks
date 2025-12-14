package com.senla.task1.factory;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Objects;

public class BeanConfigurator {

    public static <T> Class<? extends T> getImplClass(Class<T> interfaceClass) {
        try {
            String packageName = interfaceClass.getPackageName();
            String path = packageName.replace('.', '/');

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(path);

            if (url == null) {
                throw new RuntimeException("Пакет не найден: " + packageName);
            }

            File directory = new File(url.toURI());
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (!file.getName().endsWith(".class")) continue;

                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> aClass = Class.forName(className);

                if (interfaceClass.isAssignableFrom(aClass)
                        && !aClass.isInterface()
                        && !Modifier.isAbstract(aClass.getModifiers())) {
                    return (Class<? extends T>) aClass;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка поиска реализации: " + interfaceClass.getName());
        }
        throw new RuntimeException("Реализация не найдена: " + interfaceClass.getName());
    }
}
