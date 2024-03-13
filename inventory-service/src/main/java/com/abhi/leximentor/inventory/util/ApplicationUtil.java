package com.abhi.leximentor.inventory.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

@Component
@Slf4j
public class ApplicationUtil {
    public String generateRandomString(int length) {
        // Validate length
        if (length < 6) {
            throw new IllegalArgumentException("Length must be at least 6.");
        }

        // Generate the first 6 characters (A-Z or a-z)
//        String firstPart = generateRandomLetters(6);

        // Generate the remaining characters (A-Z, a-z, 0-9)
//        String remainingPart = generateRandomAlphanumeric(length - 6);

        // Concatenate the two parts
        return generateRandomAlphanumeric(length);
    }

    private String generateRandomLetters(int length) {
        Random random = new Random();
        StringBuilder letters = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char randomLetter = (char) ('A' + random.nextInt(26));
            letters.append(randomLetter);
        }

        return letters.toString();
    }

    private String generateRandomAlphanumeric(int length) {
        Random random = new Random();
        StringBuilder alphanumeric = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('A' + random.nextInt(36));
            alphanumeric.append(randomChar);
        }

        return alphanumeric.toString();
    }

    public String getDrillName() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE,dd-MMM-yyyy,HH:mm:ss", Locale.ENGLISH);
        return localDateTime.format(formatter);
    }
}
