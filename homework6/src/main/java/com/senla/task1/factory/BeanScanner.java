package com.senla.task1.factory;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class BeanScanner {

    public static Set<Class<?>> scanPackage(String basePackage) {
        try {
            Set<Class<?>> classes = new HashSet<>();
            String path = basePackage.replace('.', '/');

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);

            if (resource == null) {
                throw new RuntimeException("Пакет не найден: " + basePackage);
            }

            File rootDir = new File(resource.toURI());
            scanDirectory(rootDir, basePackage, classes);

            return classes;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сканирования пакета", e);
        }
    }

    private static void scanDirectory(File directory, String packageName, Set<Class<?>> classes) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> aClass = Class.forName(className);
                    classes.add(aClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
