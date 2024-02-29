package com.abhi.leximentor.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class WordPublisherServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordPublisherServiceApplication.class, args);
    }

}
