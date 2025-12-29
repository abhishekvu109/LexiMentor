package com.abhi.saarthi.apigateway.filter;

import com.abhi.saarthi.apigateway.dto.IntrospectionResponse;
import com.abhi.saarthi.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final WebClient webClient;          // for calling auth-service
    @Value("${gateway.security.introspection.required}")
    private boolean requireIntrospection; // config property
    @Value("${auth.validate.url}")
    private String INTROSPECT_URL;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }
        String token = extractBearerToken(exchange);

        if (token == null) {
            return unauthorized(exchange, "UNAUTHORIZED");
        }

        try {
            // Fast local validation - 99%+ of requests end here
            Claims claims = jwtUtil.validateAndGetClaims(token); // throws on invalid

            // Optional expensive revocation/introspection check
            if (shouldCallIntrospection(claims)) {
                return introspectToken(token)
                        .flatMap(response -> {
                            if (response.active()|| StringUtils.equalsIgnoreCase(response.status(),"AUTHORIZED")) {
                                enrichRequest(exchange, claims, response);
                                return chain.filter(exchange);
                            } else {
                                return forbidden(exchange, "FORBIDDEN");
                            }
                        })
                        .onErrorResume(e -> forbidden(exchange, "Introspection failed: " + e.getMessage()));
            }

            // Normal happy path - no network call
            enrichRequest(exchange, claims, null);
            return chain.filter(exchange);

        } catch (JwtException e) {
            return unauthorized(exchange,"UNAUTHORIZED");
        }
    }

    private boolean shouldCallIntrospection(Claims claims) {
        // You can make this configurable per route
        return requireIntrospection && claims.get("jti") != null;
    }


    private String extractBearerToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/v1/login") ||
                path.startsWith("/api/auth/v1/refresh") ||
                path.startsWith("/api/auth/v1/register") ||
                path.startsWith("/api/auth/v1/logout") ||
                path.startsWith("/api/auth/v1/validate") ||
                // add more public endpoints if needed
                path.startsWith("/actuator") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    private Mono<IntrospectionResponse> introspectToken(String token) {
        return webClient.post()
                .uri(INTROSPECT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("token", token))
                .retrieve()
                .bodyToMono(IntrospectionResponse.class)
                .timeout(Duration.ofMillis(800))
                .onErrorMap(e -> new RuntimeException("Introspection service unavailable", e));
    }

    private void enrichRequest(ServerWebExchange exchange, Claims claims, IntrospectionResponse introspection) {

        exchange.getAttributes().put("jwt.claims", claims); // optional - useful for downstream filters
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(("Unauthorized: " + message).getBytes())));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(("Forbidden: " + message).getBytes())));
    }

    private String getJwtErrorMessage(JwtException e) {
        if (e instanceof io.jsonwebtoken.ExpiredJwtException) {
            return "Token expired";
        } else if (e instanceof io.jsonwebtoken.MalformedJwtException) {
            return "Malformed token";
        } else if (e instanceof io.jsonwebtoken.SignatureException) {
            return "Invalid signature";
        } else {
            return e.getMessage();
        }
    }

    private Iterable<String> getRoles(Claims claims) {
        // Adapt according to how you store roles in your JWT
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof String) {
            return List.of(((String) rolesObj).split(","));
        }
        if (rolesObj instanceof List) {
            return ((List<?>) rolesObj).stream()
                    .map(Object::toString)
                    .toList();
        }
        return List.of();
    }

    private String getScopesString(Claims claims, IntrospectionResponse introspection) {
        if (introspection != null && introspection.scope() != null) {
            return String.join(" ", introspection.scope());
        }
        Object scopeObj = claims.get("scope");
        return scopeObj != null ? scopeObj.toString() : "";
    }

    @Override
    public int getOrder() {
        return -100; // Run relatively early in the chain
    }
}

