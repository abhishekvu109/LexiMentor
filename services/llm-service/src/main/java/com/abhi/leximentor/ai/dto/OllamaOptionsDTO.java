package com.abhi.leximentor.ai.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class OllamaOptionsDTO {
    private int seed;
    private int num_predict;
    private int num_ctx;
    private int top_k;
    private double top_p;
    private double min_p;
    private double typical_p;
    private int repeat_last_n;
    private double temperature;
    private double repeat_penalty;
    private double presence_penalty;
    private double frequency_penalty;
    private boolean penalize_newline;
    private boolean numa;
    private boolean use_mmap;
    private List<String> stop;
}
