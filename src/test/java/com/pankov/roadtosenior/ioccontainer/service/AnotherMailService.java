package com.pankov.roadtosenior.ioccontainer.service;

import com.pankov.roadtosenior.ioccontainer.processor.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnotherMailService extends MailService {
    private String protocol;
    private Integer port;
}

