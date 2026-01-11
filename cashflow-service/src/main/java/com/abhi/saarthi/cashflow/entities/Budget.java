package com.abhi.saarthi.cashflow.entities;

import com.abhi.saarthi.cashflow.constants.Period;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "budget")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long refId;

    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(name = "amount")
    private double amount;

    @Enumerated(EnumType.STRING)
    private Period period = Period.MONTHLY;

    private Integer year;

    private int status;

    private Integer month;          // 1-12 for monthly budgets

    private LocalDateTime createdAt = LocalDateTime.now();
}
