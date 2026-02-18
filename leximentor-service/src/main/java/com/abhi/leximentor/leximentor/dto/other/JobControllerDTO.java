package com.abhi.leximentor.leximentor.dto.other;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobControllerDTO {
    private List<String> words;
}
