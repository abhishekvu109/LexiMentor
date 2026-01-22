package com.abhi.saarthi.cashflow.dto.dashboard;

import com.abhi.saarthi.cashflow.constants.Period;

import java.time.LocalDate;

public record RecentTransaction(
        String id,
        String description,
        String category,
        Money amount,
        LocalDate date,           // or LocalDateTime if you need time too
        Period type      // helps frontend choose icon (one-time vs recurring)
) {

}
