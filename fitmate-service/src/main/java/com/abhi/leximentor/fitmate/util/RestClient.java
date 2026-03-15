package com.abhi.leximentor.fitmate.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Thin HTTP client facade backed by {@link WebClient}.
 *
 * <p>All methods block until the response arrives, preserving the same
 * synchronous contract as the previous {@code RestTemplate}-based implementation
 * so that existing callers require no changes.
 *
 * <p>Inject {@link WebClient.Builder} (not a pre-built {@code WebClient}) so
 * the shared codec / buffer configuration from {@code WebConfig} is inherited
 * while still allowing each call to set its own URI and headers.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestClient {

    private final WebClient.Builder webClientBuilder;

    public <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        return webClientBuilder.build()
                .get()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, T requestBody, Class<T> responseType) {
        log.info("The request before sending: url={}, body={}", url, requestBody);
        return webClientBuilder.build()
                .post()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    public <T> ResponseEntity<T> put(String url, HttpHeaders headers, T requestBody, Class<T> responseType) {
        return webClientBuilder.build()
                .put()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    public <T> ResponseEntity<Void> delete(String url, HttpHeaders headers) {
        return webClientBuilder.build()
                .delete()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
