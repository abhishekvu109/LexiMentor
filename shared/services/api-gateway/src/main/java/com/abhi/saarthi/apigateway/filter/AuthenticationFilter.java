package com.abhi.saarthi.apigateway.filter;

import com.abhi.saarthi.apigateway.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${auth.validate.url}")
    private String AUTH_VALIDATOR_URL;
    private final RouterValidator validator;
    private final WebClient.Builder webClientBuilder;


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange.getResponse(), HttpStatus.UNAUTHORIZED);
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                return webClientBuilder.build()
                        .get()
                        .uri(AUTH_VALIDATOR_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authHeader)
                        .retrieve()
                        .bodyToMono(AuthResponse.class)
                        .flatMap(response -> {
                            if (response.getStatus().equals("AUTHORIZED")) {
                                return chain.filter(exchange);
                            }
                            return onError(exchange.getResponse(), HttpStatus.UNAUTHORIZED);
                        }).onErrorResume(e -> onError(exchange.getResponse(), HttpStatus.UNAUTHORIZED));
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerHttpResponse response, HttpStatus status) {
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {
    }
}
