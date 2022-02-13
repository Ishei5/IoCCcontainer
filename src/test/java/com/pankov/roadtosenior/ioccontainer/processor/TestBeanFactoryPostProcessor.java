package com.pankov.roadtosenior.ioccontainer.processor;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList) {

    }
}
