package com.pankov.roadtosenior.ioccontainer;

import org.junit.jupiter.api.Test;

public class ClassPathApplicationContextITest {

    @Test
    public void test() {
        ApplicationContext context = new ClassPathApplicationContext("context.xml", "context2.xml");
        context.getBeanNames().stream()
                .map(context::getBean)
                .forEach(System.out::println);
    }
}
