package com.abhi.synapster.inventory.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor(staticName = "build")
public class CustomError {
    private String field;
    private String description;
    private String code;
}

