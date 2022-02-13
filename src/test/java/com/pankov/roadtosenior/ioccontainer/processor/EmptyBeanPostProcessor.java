package com.pankov.roadtosenior.ioccontainer.processor;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class EmptyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return null;
    }
}
