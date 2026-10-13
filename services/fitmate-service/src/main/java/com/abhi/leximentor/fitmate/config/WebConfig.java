package com.abhi.leximentor.fitmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    /**
     * Shared {@link WebClient.Builder} bean.
     *
     * <p>Configured with a 16 MB in-memory buffer limit so that large Ollama
     * responses (or any other sizable payload) are not rejected by the default
     * 256 KB codec limit.  All callers should inject {@code WebClient.Builder}
     * rather than a pre-built {@code WebClient} so they can still apply
     * per-call base-URL or header customisations before calling {@code .build()}.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies);
    }
}
