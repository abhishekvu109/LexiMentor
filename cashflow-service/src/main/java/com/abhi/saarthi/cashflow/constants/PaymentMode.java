package com.abhi.saarthi.cashflow.constants;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum PaymentMode {
    UPI("UPI"),
    DEBIT_CARD("DEBIT CARD"),
    CREDIT_CARD("CREDIT CARD"),
    CASH("CASH"),
    OTHERS("OTHERS"),
    INTERNET_BANKING("INTERNET BANKING");

    private String value;

    public static PaymentMode of(String value) {
        if (StringUtils.equalsIgnoreCase(value, UPI.value)) {
            return UPI;
        } else if (StringUtils.equalsIgnoreCase(value, DEBIT_CARD.value)) {
            return DEBIT_CARD;
        } else if (StringUtils.equalsIgnoreCase(value, CASH.value)) {
            return CASH;
        } else if (StringUtils.equalsIgnoreCase(value, CREDIT_CARD.value)) {
            return CREDIT_CARD;
        } else if (StringUtils.equalsIgnoreCase(value, INTERNET_BANKING.value)) {
            return INTERNET_BANKING;
        } else {
            return OTHERS;
        }
    }

    public static String of(PaymentMode mode) {
        return switch (mode) {
            case UPI -> UPI.value;
            case DEBIT_CARD -> DEBIT_CARD.value;
            case CASH -> CASH.value;
            case CREDIT_CARD -> CREDIT_CARD.value;
            case INTERNET_BANKING -> INTERNET_BANKING.value;
            default -> OTHERS.value;
        };
    }
}
