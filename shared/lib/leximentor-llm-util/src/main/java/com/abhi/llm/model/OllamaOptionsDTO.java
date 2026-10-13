package com.abhi.llm.model;


import java.util.List;


public record OllamaOptionsDTO(int seed, int num_predict, int num_ctx, int top_k, double top_p, double min_p,
                               double typical_p, int repeat_last_n, double temperature, double repeat_penalty,
                               double presence_penalty, double frequency_penalty, boolean penalize_newline,
                               boolean numa, boolean use_mmap, List<String> stop) {
    private static final int DEFAULT_SEED = 42;
    private static final int DEFAULT_NUM_PREDICT = 20000;
    private static final int DEFAULT_NUM_CTX = 12888;
    private static final int DEFAULT_TOP_K = 40;
    private static final double DEFAULT_TOP_P = 0.95;
    private static final double DEFAULT_MIN_P = 0.05;
    private static final double DEFAULT_TYPICAL_P = 0.7;
    private static final int DEFAULT_REPEAT_LAST_N = 33;
    private static final double DEFAULT_TEMPERATURE = 0.3;
    private static final double DEFAULT_REPEAT_PENALTY = 1.1;
    private static final double DEFAULT_PRESENCE_PENALTY = 0.5;
    private static final double DEFAULT_FREQUENCY_PENALTY = 0.5;
    private static final boolean DEFAULT_PENALIZE_NEWLINE = false;
    private static final boolean DEFAULT_NUMA = false;
    private static final boolean DEFAULT_USE_MMAP = true;
    private static final List<String> DEFAULT_STOP = List.of("user:");

    public OllamaOptionsDTO() {
        this(
                DEFAULT_SEED,
                DEFAULT_NUM_PREDICT,
                DEFAULT_NUM_CTX,
                DEFAULT_TOP_K,
                DEFAULT_TOP_P,
                DEFAULT_MIN_P,
                DEFAULT_TYPICAL_P,
                DEFAULT_REPEAT_LAST_N,
                DEFAULT_TEMPERATURE,
                DEFAULT_REPEAT_PENALTY,
                DEFAULT_PRESENCE_PENALTY,
                DEFAULT_FREQUENCY_PENALTY,
                DEFAULT_PENALIZE_NEWLINE,
                DEFAULT_NUMA,
                DEFAULT_USE_MMAP,
                DEFAULT_STOP
        );
    }
}
