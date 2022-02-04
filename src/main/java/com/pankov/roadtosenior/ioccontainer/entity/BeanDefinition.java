package com.pankov.roadtosenior.ioccontainer.entity;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BeanDefinition {
    private String id;
    private String className;
    private Map<String, String> valueProperties;
    private Map<String, String> refProperties;
}
