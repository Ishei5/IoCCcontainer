package com.pankov.roadtosenior.ioccontainer.reader;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class XMLBeanDefinitionReaderITest {

    List<BeanDefinition> expectedListBeanDefinitions = List.of(
            BeanDefinition.builder()
                    .id("mailService")
                    .className("com.pankov.roadtosenior.ioccontainer.service.MailService")
                    .valueProperties(Map.of("protocol", "POP3", "port", "3000"))
                    .refProperties(null)
                    .build(),
            BeanDefinition.builder()
                    .id("userService")
                    .className("com.pankov.roadtosenior.ioccontainer.service.UserService")
                    .valueProperties(null)
                    .refProperties(Map.of("mailService", "mailService"))
                    .build(),
            BeanDefinition.builder()
                    .id("paymentWithMaxService")
                    .className("com.pankov.roadtosenior.ioccontainer.service.PaymentService")
                    .valueProperties(Map.of("maxAmount", "5000"))
                    .refProperties(Map.of("mailService", "mailService"))
                    .build(),
            BeanDefinition.builder()
                    .id("paymentService")
                    .className("com.pankov.roadtosenior.ioccontainer.service.PaymentService")
                    .valueProperties(null)
                    .refProperties(Map.of("mailService", "mailService"))
                    .build()
    );

    @Test
    public void testGetBeanDefinitionListFrom2Files() {
        XMLBeanDefinitionReader xmlBeanDefinitionReader = new XMLBeanDefinitionReader(
                new String[]{"context.xml", "context2.xml"});
        List<BeanDefinition> beanDefinitions = xmlBeanDefinitionReader.getBeanDefinitions();

        assertNotNull(beanDefinitions);
        assertEquals(4, beanDefinitions.size());

        assertEquals(expectedListBeanDefinitions, beanDefinitions);
    }
}
