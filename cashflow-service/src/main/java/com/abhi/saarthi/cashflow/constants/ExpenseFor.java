package com.abhi.saarthi.cashflow.constants;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum ExpenseFor {
    PERSONAL("PERSONAL"),
    OTHERS("OTHERS"),
    FAMILY("FAMILY");

    private String value;

    public static ExpenseFor parse(String value) {
        if (StringUtils.equalsAnyIgnoreCase(value, PERSONAL.value)) {
            return PERSONAL;
        } else if (StringUtils.equalsIgnoreCase(value, FAMILY.value)) {
            return FAMILY;
        } else {
            return OTHERS;
        }
    }

    public static String parse(ExpenseFor expenseFor) {
        if (expenseFor == PERSONAL)
            return PERSONAL.value;
        else if (expenseFor == FAMILY)
            return FAMILY.value;
        else
            return OTHERS.value;
    }

}
