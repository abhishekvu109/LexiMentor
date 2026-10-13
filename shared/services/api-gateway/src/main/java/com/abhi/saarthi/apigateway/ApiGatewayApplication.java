package com.abhi.saarthi.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;


@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
        // Programmatic logging configuration for debugging
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("org.springframework.security").setLevel(Level.DEBUG);
        loggerContext.getLogger("org.springframework.web.reactive").setLevel(Level.DEBUG);
        loggerContext.getLogger("reactor.netty").setLevel(Level.DEBUG);
        loggerContext.getLogger("org.springframework.cloud.gateway").setLevel(Level.DEBUG);

		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
