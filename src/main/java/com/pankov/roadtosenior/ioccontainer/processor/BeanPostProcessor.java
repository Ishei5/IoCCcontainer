package com.pankov.roadtosenior.ioccontainer.processor;

public interface BeanPostProcessor extends SystemBean{
    public Object postProcessBeforeInitialization(Object bean, String beanName);

    public Object postProcessAfterInitialization(Object bean, String beanName);
}
