package com.pankov.roadtosenior.ioccontainer;

import java.util.List;

public interface ApplicationContext {

    <T> T getBean(Class<T> clazz);

    Object getBean(String id);

    <T> T getBean(Class<T> clazz, String className);

    List<String> getBeanNames();
}
