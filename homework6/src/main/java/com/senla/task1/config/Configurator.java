package com.senla.task1.config;

import com.senla.task1.annotations.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class Configurator {

    public static void configure(Object object) {
        Class<?> c = object.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ConfigProperty.class)) {
                continue;
            }
            ConfigProperty configProperty = field.getAnnotation(ConfigProperty.class);

            String fileName = configProperty.configFileName();
            String propertyName = configProperty.propertyName();
            Class<?> type = field.getType();

            Properties properties = new Properties();
            try (InputStream inputStream = Configurator.class.getClassLoader().getResourceAsStream(fileName)) {
                properties.load(inputStream);
                String value = properties.getProperty(propertyName);
                Object convertedValue = convert(value, type, field);
                try {
                    field.setAccessible(true);
                    field.set(object, convertedValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Ошибка при конфигурации поля " + field.getName());
                }
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при загрузке файла конфигурации", e);
            }
        }
    }

    private static Object convert(String value, Class<?> type, Field field) {

        if (type == String.class) {
            return value;
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }

        if (type.isArray()) {
            String[] parts = value.split(",");
            Class<?> component = type.getComponentType();
            Object array = Array.newInstance(component, parts.length);
            for (int i = 0; i < parts.length; i++) {
                String elem = parts[i].trim();
                Object converted = convert(elem, component, field);
                Array.set(array, i, converted);
            }
            return array;
        }

        if (Collection.class.isAssignableFrom(type)) {

            if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
                Type elementType = parameterizedType.getActualTypeArguments()[0];
                Class<?> elementClass = (Class<?>) elementType;
                List<Object> list = new ArrayList<>();
                for (String part : value.split(",")) {
                    list.add(convert(part.trim(), elementClass, field));
                }
                return list;
            }
            return Arrays.asList(value.split(","));
        }
        return value;
    }

}
