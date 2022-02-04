package com.pankov.roadtosenior.ioccontainer.reader;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    
    List<BeanDefinition> getBeanDefinitions();
}
