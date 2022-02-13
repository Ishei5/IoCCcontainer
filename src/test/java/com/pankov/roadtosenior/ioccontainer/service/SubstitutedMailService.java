package com.pankov.roadtosenior.ioccontainer.service;

import com.pankov.roadtosenior.ioccontainer.processor.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstitutedMailService extends MailService {
    private String protocol;
    private Integer port;

    @PostConstruct
    private void init() {
        protocol = "SMTP";
    }
}