package com.pankov.roadtosenior.ioccontainer.reader;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import com.pankov.roadtosenior.ioccontainer.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XMLBeanDefinitionReaderTest {

    private static final String correctXml = """
            <beans>
            	<bean id="mailService" class="com.pankov.roadtosenior.ioccontainer.service.MailService">
            		<property name="protocol" value="POP3"/>
            		<property name="port" value="3000"/>
            	</bean>
            	
            	<bean id="userService" class="com.pankov.roadtosenior.ioccontainer.service.UserService">
            		<property name="mailService" ref="mailService"/>
            	</bean>
            	
            	<bean id="paymentWithMaxService" class="com.pankov.roadtosenior.ioccontainer.service.PaymentService">
            		<property name="maxAmount" value="5000"/>
            		<property name="mailService" ref="mailService"/>
            	</bean>
            	
            	<bean id="paymentService" class="com.pankov.roadtosenior.ioccontainer.service.PaymentService">
            		<property name="mailService" ref="mailService"/>
            	</bean>
            </beans>
            """;

    private static final String xmlWithoutIdAttribute = """
            <beans>
            	<bean class="com.pankov.roadtosenior.ioccontainer.service.MailService">
            		<property name="protocol" value="POP3"/>
            		<property name="port" value="3000"/>
            	</bean>
            """;

    private static final String xmlWithoutClassAttribute = """
            <beans>
            	<bean id="mailService">
            		<property name="protocol" value="POP3"/>
            		<property name="port" value="3000"/>
            	</bean>
            """;

    @Test
    public void testParseXMLToBeanDefinitionList() {
        List<BeanDefinition> expectedListBeanDefinitions = List.of(
                BeanDefinition.builder()
                        .id("mailService")
                        .className("com.pankov.roadtosenior.ioccontainer.service.MailService")
                        .valueProperties(Map.of("protocol", "POP3", "port", "3000"))
                        .refProperties(null)
                        .build(),
                BeanDefinition.builder()
                        .id("userService")
                        .className("com.pankov.roadtosenior.ioccontainer.UserService")
                        .valueProperties(null)
                        .refProperties(Map.of("mailService", "mailService"))
                        .build(),
                BeanDefinition.builder()
                        .id("paymentWithMaxService")
                        .className("com.pankov.roadtosenior.ioccontainer.PaymentService")
                        .valueProperties(Map.of("maxAmount", "5000"))
                        .refProperties(Map.of("mailService", "mailService"))
                        .build(),
                BeanDefinition.builder()
                        .id("paymentService")
                        .className("com.pankov.roadtosenior.ioccontainer.PaymentService")
                        .valueProperties(null)
                        .refProperties(Map.of("mailService", "mailService"))
                        .build());

        SaxXMLBeanDefinitionReader reader = new SaxXMLBeanDefinitionReader();
        List<BeanDefinition> actualListBeanDefinitions = reader.parseXMLToBeanDefinitionList(
                new ByteArrayInputStream(correctXml.getBytes(StandardCharsets.UTF_8)));

        assertNotNull(actualListBeanDefinitions);

        assertEquals(4, actualListBeanDefinitions.size());

        for (int i = 0; i < actualListBeanDefinitions.size(); i++) {
            assertEquals(expectedListBeanDefinitions.get(0), actualListBeanDefinitions.get(0));
        }
    }


    @Test
    public void testShouldThrowParseExceptionWhenIdAttributeMissing () {
        assertThrows(ParseException.class, () ->
                new XMLBeanDefinitionReader().parseXMLToBeanDefinitionList(
                        new ByteArrayInputStream(xmlWithoutIdAttribute.getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    public void testShouldThrowParseExceptionWhenClassAttributeMissing () {
        assertThrows(ParseException.class, () ->
                new XMLBeanDefinitionReader().parseXMLToBeanDefinitionList(
                        new ByteArrayInputStream(xmlWithoutClassAttribute.getBytes(StandardCharsets.UTF_8))));
    }
}