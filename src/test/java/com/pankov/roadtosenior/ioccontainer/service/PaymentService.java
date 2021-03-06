package com.pankov.roadtosenior.ioccontainer.service;

import com.pankov.roadtosenior.ioccontainer.processor.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentService {
    private int maxAmount;
    private MailService mailService;

    @PostConstruct
    private void init(){
        maxAmount = Integer.MAX_VALUE;
    }
}
