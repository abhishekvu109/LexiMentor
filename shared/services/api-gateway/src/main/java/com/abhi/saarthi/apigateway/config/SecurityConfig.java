package com.abhi.saarthi.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())           // ← this is the correct way
                .authorizeExchange(exchanges ->
                        exchanges.anyExchange().permitAll() // or your rules
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        return http.build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Option A: Development (temporary - less secure)
         config.addAllowedOrigin("*");

        // Option B: Production / recommended
//        config.addAllowedOrigin("http://localhost:3000");         // ← your frontend
//        config.addAllowedOrigin("https://your-frontend-domain.com");
//        config.addAllowedOriginPattern("http://192.168.*.*:*");   // optional: local network

        config.setAllowCredentials(false);                         // keep if using cookies/auth
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");                             // or list: GET, POST, PUT, ...
        config.setMaxAge(3600L);                                  // cache preflight 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);          // apply to everything

        return new CorsWebFilter(source);
    }
}