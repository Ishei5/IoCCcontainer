package com.pankov.roadtosenior.ioccontainer;

import com.pankov.roadtosenior.ioccontainer.service.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClassPathApplicationContextITest {

    @Test
    public void testApplicationContext() {
        ApplicationContext context = new ClassPathApplicationContext("context.xml", "context2.xml");

        assertEquals(4, context.getBeanNames().size());

        Object userService = context.getBean("userService");
        MailService mailService = ((UserService) userService).getMailService();

        assertEquals(3000, mailService.getPort());
        assertEquals("POP3", mailService.getProtocol());

        MailService mailService_ = context.getBean(MailService.class);
        assertEquals(mailService, mailService_);

        PaymentService paymentWithMaxService = context.getBean(PaymentService.class, "paymentWithMaxService");
        assertEquals(Integer.MAX_VALUE, paymentWithMaxService.getMaxAmount());
        assertEquals(mailService, paymentWithMaxService.getMailService());

        PaymentService paymentService = context.getBean(PaymentService.class, "paymentService");
        assertEquals(paymentWithMaxService.getMailService(), paymentService.getMailService());
    }

    @Test
    public void testApplicationContextWithSystemBeans() {
        ClassPathApplicationContext context = new ClassPathApplicationContext("context_for_test_processors.xml");

        // Check BeanFactoryPostProcessor
        assertEquals("com.pankov.roadtosenior.ioccontainer.service.SubstitutedMailService",
                context.getBeanDefinitions().stream()
                        .filter(beanDefinition -> "mailService".equals(beanDefinition.getId()))
                        .findAny().get().getClassName());

        assertEquals(SubstitutedMailService.class, context.getBean("mailService").getClass());

        //Check BeanPostProcessor
        PaymentService paymentWithMaxService = PaymentService.class.cast(context.getBean("paymentWithMaxService"));
        assertEquals(AnotherMailService.class, paymentWithMaxService.getMailService().getClass());
        assertEquals(-1, paymentWithMaxService.getMaxAmount());

        //Check @PostConstruct
        PaymentService paymentService = PaymentService.class.cast(context.getBean("paymentService"));
        assertEquals(Integer.MAX_VALUE, paymentService.getMaxAmount());
        assertEquals("SMTP", paymentService.getMailService().getProtocol());
    }
}
