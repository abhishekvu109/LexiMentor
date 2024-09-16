package com.abhi.leximentor.ai.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class Meta {
    private String code;
    private String description;
    private int status;
}

