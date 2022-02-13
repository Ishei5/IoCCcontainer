package com.pankov.roadtosenior.ioccontainer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bean {
    private String id;
    private Object value;
}
