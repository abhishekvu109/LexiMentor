package com.abhi.saarthi.cashflow.constants;

import org.apache.commons.lang3.StringUtils;

public enum Currency {
    INR("INR"),
    USD("USD"),
    GBP("GBP"),
    EUR("EUR");

    private final String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public static Currency parse(String currency) {
        if (StringUtils.equalsIgnoreCase(currency, INR.currency)) {
            return INR;
        } else if (StringUtils.equalsIgnoreCase(currency, USD.currency)) {
            return USD;
        } else if (StringUtils.equalsIgnoreCase(currency, GBP.currency)) {
            return GBP;
        } else {
            return EUR;
        }
    }

    public static String parse(Currency currency) {
        if (currency == INR) {
            return INR.currency;
        } else if (currency == USD) {
            return USD.currency;
        } else if (currency == GBP) {
            return GBP.currency;
        } else {
            return EUR.currency;
        }
    }
}
