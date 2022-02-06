package com.pankov.roadtosenior.ioccontainer;

import com.pankov.roadtosenior.ioccontainer.entity.Bean;
import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import com.pankov.roadtosenior.ioccontainer.exception.BeanException;
import com.pankov.roadtosenior.ioccontainer.exception.BeanInstantiationException;
import com.pankov.roadtosenior.ioccontainer.exception.NoUniqueBeanException;
import com.pankov.roadtosenior.ioccontainer.service.MailService;
import com.pankov.roadtosenior.ioccontainer.service.PaymentService;
import com.pankov.roadtosenior.ioccontainer.service.ServiceWithoutDefConstructor;
import com.pankov.roadtosenior.ioccontainer.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathApplicationContextTest {

    private ClassPathApplicationContext context;

    private List<BeanDefinition> expectedListBeanDefinitions = List.of(
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
                    .id("paymentService")
                    .className("com.pankov.roadtosenior.ioccontainer.service.PaymentService")
                    .valueProperties(null)
                    .refProperties(Map.of("mailService", "mailService"))
                    .build(),
            BeanDefinition.builder()
                    .id("paymentWithMaxService")
                    .className("com.pankov.roadtosenior.ioccontainer.service.PaymentService")
                    .valueProperties(Map.of("maxAmount", "5000"))
                    .refProperties(Map.of("mailService", "mailService"))
                    .build());

    private List<Bean> expectedListBeans = List.of(
            Bean.builder()
                    .id("mailService")
                    .value(new MailService())
                    .build(),
            Bean.builder()
                    .id("userService")
                    .value(new UserService())
                    .build(),
            Bean.builder()
                    .id("paymentService")
                    .value(new PaymentService())
                    .build(),
            Bean.builder()
                    .id("paymentWithMaxService")
                    .value(new PaymentService())
                    .build());

    @BeforeEach
    public void before() {
        context = new ClassPathApplicationContext();
    }

    @Test
    public void testCreateBean_shouldCreateBeanForMailService() {
        BeanDefinition beanDefinition = expectedListBeanDefinitions.get(0);

        Bean expectedBean = expectedListBeans.get(0);
        Bean actualBean = context.createBean(beanDefinition);

        assertEquals(expectedBean, actualBean);
    }

    @Test
    public void testCreateBean_shouldThrowBeanInstantiationException() {
        assertThrows(BeanInstantiationException.class, () -> context.createBean(
                BeanDefinition.builder()
                        .id("service")
                        .className("com.pankov.roadtosenior.ioccontainer.service.ServiceWithoutDefConstructor")
                        .valueProperties(null)
                        .refProperties(null)
                        .build()));
    }

    @Test
    public void testCreateBeans_shouldCreateListOfBeans() {
        List<Bean> actualListBeans = context.createBeans(expectedListBeanDefinitions);

        assertNotNull(actualListBeans);

        assertEquals(4, actualListBeans.size());
        for (int i = 0; i < expectedListBeans.size(); i++) {
            assertEquals(expectedListBeans.get(i), actualListBeans.get(i));
        }
    }

    @Test
    public void testCreateSetter() {
        String setter = context.createSetterName("maxAmount");
        assertEquals("setMaxAmount", setter);
    }

    @Test
    @SneakyThrows
    public void testSetterMethod() {
        PaymentService paymentService = new PaymentService();
        assertEquals(0, paymentService.getMaxAmount());
        Method setMaxAmount = context.getSetterMethod(paymentService, "setMaxAmount", int.class);
        setMaxAmount.invoke(paymentService, 111);
        assertEquals(111, paymentService.getMaxAmount());
    }

    @Test
    public void testGetSetterMethodShouldThrowException() {
        assertThrows(BeanInstantiationException.class, () ->
                context.getSetterMethod(new ServiceWithoutDefConstructor("POP3", 3333),
                        "setPort", int.class));
    }

    @Test
    public void testGetStringValueType() {
        MailService mailService = new MailService();
        Class<?> actualType = context.getValueType(mailService, "protocol");
        assertEquals(String.class, actualType);
    }

    @Test
    public void testGetIntValueType() {
        PaymentService paymentService = new PaymentService();
        Class<?> actualType = context.getValueType(paymentService, "maxAmount");
        assertEquals(int.class, actualType);
    }

    @Test
    public void testGetReferenceValueType() {
        PaymentService paymentService = new PaymentService();
        Class<?> actualType = context.getValueType(paymentService, "mailService");
        assertEquals(MailService.class, actualType);
    }

    @Test
    public void testParseIntegerProperty() {
        Object actual = context.parseProperty("111", int.class);

        assertEquals(111, actual);
        assertEquals(Integer.class, actual.getClass());
    }

    @Test
    public void testParseDoubleProperty() {
        Object actual = context.parseProperty("111.11", double.class);

        assertEquals(111.11, actual);
        assertEquals(Double.class, actual.getClass());
    }


    @Test
    public void testInjectValue() {
        MailService mailService = new MailService();
        context.injectValueProperty(mailService, "protocol", "POP3");
        context.injectValueProperty(mailService, "port", "3000");

        assertEquals(new MailService("POP3", 3000), mailService);
    }

    @Test
    public void testInjectRefProperty_shouldThrowBeanInstantiationException() {
        context.setBeans(Collections.emptyList());
        assertThrows(BeanException.class, () ->
                context.injectRefProperty(new PaymentService(), "mailService", "mailService"));
    }

    @Test
    public void testInjectRefProperty() {
        List<Bean> beans = List.of(new Bean("mailService", new MailService()));
        context.setBeans(beans);
        UserService userService = new UserService();

        assertNull(userService.getMailService());

        context.injectRefProperty(userService, "mailService", "mailService");
        MailService injectedMailService = userService.getMailService();
        assertNotNull(injectedMailService);
        assertEquals(MailService.class, injectedMailService.getClass());
    }

    @Test
    public void testInjectValueProperties() {
        context.injectValueProperties(expectedListBeanDefinitions, expectedListBeans);

        MailService mailService = (MailService) expectedListBeans.get(0).getValue();
        assertEquals(3000, mailService.getPort());

        assertEquals(5000, ((PaymentService) expectedListBeans.get(3).getValue()).getMaxAmount());
    }

    @Test
    public void testInjectRefProperties() {
        context.setBeans(expectedListBeans);
        context.injectValueProperty(expectedListBeans.get(3).getValue(), "maxAmount", "1000");
        context.injectRefProperties(expectedListBeanDefinitions, expectedListBeans);

        assertEquals(MailService.class, ((UserService) expectedListBeans.get(1).getValue()).getMailService().getClass());
        assertEquals(MailService.class, ((PaymentService) expectedListBeans.get(3).getValue()).getMailService().getClass());
        assertEquals(1000, ((PaymentService) expectedListBeans.get(3).getValue()).getMaxAmount());
    }

    @Test
    public void testGetBeanNames() {
        context.setBeans(expectedListBeans);
        assertEquals(List.of("mailService", "userService", "paymentService", "paymentWithMaxService"),
                context.getBeanNames());
    }

    @Test
    public void testGetBeanByClass() {
        context.setBeans(expectedListBeans);
        UserService bean = context.getBean(UserService.class);
        assertEquals(expectedListBeans.get(1).getValue(), bean);
    }

    @Test
    public void testGetBeanClass_shouldThrowNoUniqueException() {
        context.setBeans(expectedListBeans);
        assertThrows(NoUniqueBeanException.class, () -> context.getBean(PaymentService.class));
    }

    @Test
    public void testGetBeanByNameAndClass() {
        context.setBeans(expectedListBeans);
        assertEquals(expectedListBeans.get(2).getValue(), context.getBean(PaymentService.class, "paymentService"));

    }

}