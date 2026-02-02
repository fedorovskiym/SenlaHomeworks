package com.senla.task1.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomApplicationContext {

    private final Map<Class<?>, Object> beanMap = new HashMap<>();

    public CustomApplicationContext() {
    }

    public void init(String basePackage) {
        Set<Class<?>> classes = BeanScanner.scanPackage(basePackage);

        for (Class<?> aClass : classes) {
            beanMap.putIfAbsent(aClass, null);
        }

        CustomBeanFactory factory = (CustomBeanFactory) beanMap.get(CustomBeanFactory.class);

        if (factory == null) {
            try {
                factory = CustomBeanFactory.class.getDeclaredConstructor().newInstance();
                beanMap.put(CustomBeanFactory.class, factory);
            } catch (Exception e) {
                throw new RuntimeException("Не удалось создать CustomBeanFactory автоматически", e);
            }
        }

        factory.setContext(this);

        for (Class<?> aClass : classes) {
            if (aClass == CustomBeanFactory.class) {
                continue;
            }
            if (beanMap.get(aClass) == null) {
                Object bean = factory.createBean(aClass);
                beanMap.put(aClass, bean);
                factory.injectFields(bean);
            }
        }

        beanMap.values().forEach(factory::invokePostConstruct);
    }


    public <T> T getBean(Class<T> type) {
        Object bean = beanMap.get(type);
        if (bean != null) return (T) bean;

        CustomBeanFactory factory = (CustomBeanFactory) beanMap.get(CustomBeanFactory.class);
        if (factory == null) {
            throw new IllegalStateException("CustomBeanFactory еще не создан");
        }
        Object createdBean = factory.createBean(type);
        beanMap.put(type, createdBean);
        factory.injectFields(createdBean);
        return (T) createdBean;
    }
}
