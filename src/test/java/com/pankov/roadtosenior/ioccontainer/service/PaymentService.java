package com.pankov.roadtosenior.ioccontainer.service;

import lombok.Data;

@Data
public class PaymentService {
    private int maxAmount;
    private MailService mailService;
}
