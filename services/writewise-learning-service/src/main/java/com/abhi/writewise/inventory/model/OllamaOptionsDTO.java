package com.abhi.writewise.inventory.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Builder
public record OllamaOptionsDTO(int seed, int num_predict, int num_ctx, int top_k, double top_p, double min_p,
                               double typical_p, int repeat_last_n, double temperature, double repeat_penalty,
                               double presence_penalty, double frequency_penalty, boolean penalize_newline,
                               boolean numa, boolean use_mmap, List<String> stop) {
}
