package com.abhi.saarthi.cashflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig {

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*") // Allow requests from any origin
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//            }
//        };
//    }

//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        // Option A: Development (temporary - less secure)
//        // config.addAllowedOrigin("*");
//
//        // Option B: Production / recommended
//        config.addAllowedOrigin("http://localhost:3000");         // ← your frontend
//        config.addAllowedOrigin("https://your-frontend-domain.com");
//        config.addAllowedOriginPattern("http://192.168.*.*:*");   // optional: local network
//
//        config.setAllowCredentials(true);                         // keep if using cookies/auth
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");                             // or list: GET, POST, PUT, ...
//        config.setMaxAge(3600L);                                  // cache preflight 1 hour
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);          // apply to everything
//
//        return new CorsWebFilter(source);
//    }
}