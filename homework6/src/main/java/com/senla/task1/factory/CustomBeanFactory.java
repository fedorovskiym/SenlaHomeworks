package com.senla.task1.factory;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.FieldInject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.config.Configurator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CustomBeanFactory {

    @FieldInject
    private ApplicationContext context;

    public CustomBeanFactory() {
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public Object createBean(Class<?> beanClass) {
        try {
            if (beanClass.isEnum() || beanClass.isAnnotation() || beanClass.isAnonymousClass()
                    || Throwable.class.isAssignableFrom(beanClass)
                    || (beanClass.isMemberClass() && !Modifier.isStatic(beanClass.getModifiers())) || Modifier.isAbstract(beanClass.getModifiers())) {
                return null;
            }

            Class<?> implClass = beanClass;
            if (beanClass.isInterface()) {
                implClass = BeanConfigurator.getImplClass(beanClass);
            }
            Object bean = null;
            for (Constructor<?> constructor : implClass.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(Inject.class)) {
                    constructor.setAccessible(true);
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    Object[] params = new Object[paramTypes.length];
                    for (int i = 0; i < paramTypes.length; i++) {
                        params[i] = context.getBean(paramTypes[i]);
                    }
                    bean = constructor.newInstance(params);
                    break;
                }
            }

            if (bean == null) {
                Constructor<?> defaultConstructor = implClass.getDeclaredConstructor();
                defaultConstructor.setAccessible(true);
                bean = defaultConstructor.newInstance();
            }
            Configurator.configure(bean);
            System.out.println("Сконфигурирован " + bean.getClass().getName());
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания бина: " + beanClass.getName(), e);
        }
    }

    public void injectFields(Object bean) {
        if (bean == null) {
            return;
        }
        try {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(FieldInject.class)) {
                    field.setAccessible(true);
                    Object dependency = context.getBean(field.getType());
                    field.set(bean, dependency);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при инъекции полей", e);
        }
    }

    public void invokePostConstruct(Object bean) {
        if (bean == null) {
            return;
        }
        try {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.setAccessible(true);
                    method.invoke(bean);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка @PostConstruct", e);
        }
    }
}
