package com.pankov.roadtosenior.ioccontainer;

import com.pankov.roadtosenior.ioccontainer.service.MailService;
import com.pankov.roadtosenior.ioccontainer.service.PaymentService;
import com.pankov.roadtosenior.ioccontainer.service.UserService;
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
        assertEquals(5000, paymentWithMaxService.getMaxAmount());
        assertEquals(mailService, paymentWithMaxService.getMailService());

        PaymentService paymentService = context.getBean(PaymentService.class, "paymentService");
        assertEquals(paymentWithMaxService.getMailService(), paymentService.getMailService());
    }
}
