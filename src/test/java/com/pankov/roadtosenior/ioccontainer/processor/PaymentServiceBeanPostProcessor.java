package com.pankov.roadtosenior.ioccontainer.processor;

import com.pankov.roadtosenior.ioccontainer.service.AnotherMailService;
import com.pankov.roadtosenior.ioccontainer.service.PaymentService;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PaymentServiceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.equals("paymentWithMaxService")) {
            PaymentService.class.cast(bean).setMailService(new AnotherMailService());
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.equals("paymentWithMaxService")) {
            PaymentService paymentService = (PaymentService) bean;
            "SMTP".equals(paymentService.getMailService().getProtocol());
            paymentService.setMaxAmount(-1);
        }

        return bean;
    }
}
