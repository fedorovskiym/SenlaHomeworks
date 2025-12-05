package com.senla.task1.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private CustomBeanFactory customBeanFactory;
    private final Map<Class, Object> beanMap = new ConcurrentHashMap<>();

    public ApplicationContext() {
    }

    public void setCustomBeanFactory(CustomBeanFactory customBeanFactory) {
        this.customBeanFactory = customBeanFactory;
    }

    public <T> T getBean(Class<T> tClass) {
        if (beanMap.containsKey(tClass)) {
            return (T) beanMap.get(tClass);
        }
        T bean = customBeanFactory.getBean(tClass);
        beanMap.put(tClass, bean);
        return bean;
    }
}
