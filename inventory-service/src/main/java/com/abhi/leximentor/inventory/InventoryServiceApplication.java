package com.abhi.leximentor.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE,dd-MMM-yyyy,HH:mm:ss", Locale.ENGLISH);
        String formattedDateTime = localDateTime.format(formatter);

        // Print the formatted date and time
        System.out.println(formattedDateTime);
//        SpringApplication.run(InventoryServiceApplication.class, args);
    }

}
