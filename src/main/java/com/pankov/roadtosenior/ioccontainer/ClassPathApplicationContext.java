package com.pankov.roadtosenior.ioccontainer;

import com.pankov.roadtosenior.ioccontainer.entity.Bean;
import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import com.pankov.roadtosenior.ioccontainer.exception.BeanException;
import com.pankov.roadtosenior.ioccontainer.exception.BeanInstantiationException;
import com.pankov.roadtosenior.ioccontainer.exception.NoUniqueBeanException;
import com.pankov.roadtosenior.ioccontainer.reader.BeanDefinitionReader;
import com.pankov.roadtosenior.ioccontainer.reader.XMLBeanDefinitionReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassPathApplicationContext implements ApplicationContext {

    private List<Bean> beans;

    void setBeans(List<Bean> beans) {
        this.beans = beans;
    }

    public ClassPathApplicationContext(String... paths) {
        BeanDefinitionReader reader = new XMLBeanDefinitionReader(paths);
        List<BeanDefinition> beanDefinitions = reader.getBeanDefinitions();
        beans = createBeans(beanDefinitions);
        injectValueProperties(beanDefinitions, beans);
        injectRefProperties(beanDefinitions, beans);
    }

    public Bean createBean(BeanDefinition beanDefinition) {
        Class<?> clazz;
        Object createdObject;
        try {
            clazz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Declared class name does not exist {}", exception);
        }

        try {
            createdObject = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException
                exception) {
            throw new BeanInstantiationException("Cannot get constructor without arguments", exception);
        }

        return Bean.builder()
                .id(beanDefinition.getId())
                .value(createdObject)
                .build();
    }

    public List<Bean> createBeans(List<BeanDefinition> beanDefinitionList) {
        return beanDefinitionList.stream()
                .map(this::createBean)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) {
        List<Object> beanList = beans.stream()
                .map(Bean::getValue)
                .filter(value -> clazz.equals(value.getClass())).toList();

        if (beanList.size() > 1) {
            throw new NoUniqueBeanException("Bean with {} class not unique");
        }

        return (T) beanList.get(0);
    }

    @Override
    public Object getBean(String id) {
        return beans.stream()
                .filter(bean -> Objects.equals(id, bean.getId()))
                .map(Bean::getValue)
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz, String id) {
        return (T) beans.stream()
                .filter(bean -> Objects.equals(clazz, bean.getValue().getClass()))
                .filter(bean -> id.equals(bean.getId()))
                .map(Bean::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<String> getBeanNames() {
        return beans.stream()
                .map(Bean::getId)
                .collect(Collectors.toList());
    }

    public void injectValueProperties(List<BeanDefinition> beanDefinitionList, List<Bean> beanList) {
        for (Bean bean : beanList) {
            BeanDefinition beanDefinition = beanDefinitionList.stream()
                    .filter(bDefinition -> bean.getId().equals(bDefinition.getId()))
                    .findAny()
                    .get();
            Map<String, String> valueProperties = beanDefinition.getValueProperties();

            if (valueProperties == null) {
                continue;
            }

            valueProperties.forEach((key, value) -> injectValueProperty(bean.getValue(), key, value));
        }
    }

    public void injectRefProperties(List<BeanDefinition> beanDefinitionList, List<Bean> beanList) {
        for (Bean bean : beanList) {
            BeanDefinition beanDefinition = beanDefinitionList.stream()
                    .filter(bDefinition -> bean.getId().equals(bDefinition.getId()))
                    .findAny()
                    .get();
            Map<String, String> refProperties = beanDefinition.getRefProperties();

            if (refProperties == null) {
                continue;
            }

            refProperties.forEach((key, value) -> injectRefProperty(bean.getValue(), key, value));
        }
    }

    void injectValueProperty(Object object, String fieldName, String property) {
        String setterName = createSetterName(fieldName);
        Class<?> setterValueType = getValueType(object, fieldName);
        Method setter = getSetterMethod(object, setterName, setterValueType);

        try {
            setter.invoke(object, parseProperty(property, setterValueType));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new BeanInstantiationException(String.format("Error during set property - %s", property),
                    exception);
        }
    }

    public void injectRefProperty(Object object, String fieldName, String property) {
        String setterName = createSetterName(fieldName);
        Class<?> setterRefType = getValueType(object, fieldName);
        Method setter = getSetterMethod(object, setterName, setterRefType);

        Object value = getBean(property);
        if (value == null) {
            throw new BeanException(String.format("There is no bean with id = %s", property));
        }

        try {
            setter.invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new BeanInstantiationException(String.format("Error during set property - %s", property),
                    exception);
        }

    }

    String createSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }


    public Class<?> getValueType(Object object, String fieldName) {
        try {
            return object.getClass().getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException exception) {
            throw new BeanInstantiationException(String.format("Field \"%s\" does not exist in class", fieldName), exception);
        }
    }

    Method getSetterMethod(Object object, String methodName, Class<?> valueType) {
        try {
            return object.getClass().getDeclaredMethod(methodName, valueType);
        } catch (NoSuchMethodException exception) {
            throw new BeanInstantiationException("Setter is not accessible or does not exist", exception);
        }
    }

    Object parseProperty(String property, Class<?> propertyType) {
        Class<?> clazz = primitiveToWrapper(propertyType);
        List<Class<?>> listOfWrappers =
                List.of(Integer.class, Double.class, Long.class, Boolean.class, Float.class, Short.class, Byte.class, Character.class);
        if (!listOfWrappers.contains(clazz)) {
            return property;
        }

        try {
            return clazz.getDeclaredMethod("valueOf", String.class).invoke(clazz, property);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new BeanInstantiationException("Error during parse property", exception);
        }
    }


    @SuppressWarnings("unchecked")
    private <T> Class<T> primitiveToWrapper(final Class<T> type) {
        if (type == null || !type.isPrimitive()) {
            return type;
        }

        if (type == Integer.TYPE) {
            return (Class<T>) Integer.class;
        }
        if (type == Double.TYPE) {
            return (Class<T>) Double.class;
        }
        if (type == Long.TYPE) {
            return (Class<T>) Long.class;
        }
        if (type == Boolean.TYPE) {
            return (Class<T>) Boolean.class;
        }
        if (type == Float.TYPE) {
            return (Class<T>) Float.class;
        }
        if (type == Short.TYPE) {
            return (Class<T>) Short.class;
        }
        if (type == Byte.TYPE) {
            return (Class<T>) Byte.class;
        }
        if (type == Character.TYPE) {
            return (Class<T>) Character.class;
        }
        return type;
    }
}