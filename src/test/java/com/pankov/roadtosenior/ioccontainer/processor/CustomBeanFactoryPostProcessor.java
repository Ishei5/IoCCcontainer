package com.pankov.roadtosenior.ioccontainer.processor;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

@EqualsAndHashCode
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList) {
        for(BeanDefinition beanDefinition : beanDefinitionList) {
            if (Objects.equals("mailService", beanDefinition.getId())) {
                beanDefinition.setClassName("com.pankov.roadtosenior.ioccontainer.service.SubstitutedMailService");
            }
        }
    }
}
