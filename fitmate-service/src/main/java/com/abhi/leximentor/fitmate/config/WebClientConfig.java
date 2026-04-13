package com.abhi.leximentor.fitmate.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * WebClient beans for fitmate's external HTTP calls.
 *
 * <h3>Why WebClient instead of RestTemplate?</h3>
 * {@code RestTemplate} is in maintenance-only mode since Spring 5.
 * {@code WebClient} is the recommended replacement and is already on the
 * classpath via {@code spring-boot-starter-webflux} (added for Ollama).
 * We use it in blocking mode ({@code .block()}) here because the service
 * is Spring MVC (not reactive), which is a fully supported pattern.
 */
@Configuration
public class WebClientConfig {

    // ── fitmate-ml (Python FastAPI) ───────────────────────────────────────────

    @Value("${fitmate.ml-service.base-url:http://localhost:8001}")
    private String mlServiceBaseUrl;

    @Value("${fitmate.ml-service.connect-timeout-ms:3000}")
    private int mlConnectTimeoutMs;

    @Value("${fitmate.ml-service.read-timeout-ms:10000}")
    private int mlReadTimeoutMs;

    /**
     * WebClient pre-configured for the fitmate-ml microservice.
     * Injected as {@code mlServiceWebClient} wherever needed.
     */
    @Bean
    public WebClient mlServiceWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, mlConnectTimeoutMs)
                .responseTimeout(Duration.ofMillis(mlReadTimeoutMs));

        return WebClient.builder()
                .baseUrl(mlServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
