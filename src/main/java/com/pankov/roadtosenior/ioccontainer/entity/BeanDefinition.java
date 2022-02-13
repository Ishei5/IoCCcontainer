package com.pankov.roadtosenior.ioccontainer.entity;

import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor

public class BeanDefinition {
    @NonNull
    private final String id;
    private String className;
    private Map<String, String> valueProperties;
    private Map<String, String> refProperties;
}
