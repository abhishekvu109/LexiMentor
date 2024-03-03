package com.abhi.leximentor.inventory.dto.other;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class JobControllerDTO {
    private List<String> words;
}
