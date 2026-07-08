package com.abhi.leximentor.fitmate.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FitmateUtil {
    public static LocalDate getLocalDateFromString(String format, String date) {
        String formatter = StringUtils.isNotEmpty(format) ? format : "dd/MM/yyyy";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        return LocalDate.parse(date, dateTimeFormatter);
    }
}
