package com.abhi.writewise.inventory.model;

import lombok.*;

@Builder
@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class PromptResponse {
    private String response;
}
