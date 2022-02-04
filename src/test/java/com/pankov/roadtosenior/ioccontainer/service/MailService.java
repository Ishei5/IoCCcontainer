package com.pankov.roadtosenior.ioccontainer.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailService {
    private String protocol;
    private Integer port;
}
