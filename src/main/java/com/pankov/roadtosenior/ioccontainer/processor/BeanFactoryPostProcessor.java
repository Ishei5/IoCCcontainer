package com.pankov.roadtosenior.ioccontainer.processor;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;

import java.util.List;

@FunctionalInterface
public interface BeanFactoryPostProcessor extends SystemBean{
    void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList);
}
