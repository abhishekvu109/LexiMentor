package com.abhi.saarthi.cashflow.entities;

import com.abhi.saarthi.cashflow.constants.Period;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget")
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(name = "amount")
    private double amount;

    @Column(name = "period")
    @Enumerated(EnumType.STRING)
    private Period period = Period.MONTHLY;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "year")
    private Integer year;

    @Column(name = "status")
    private int status;

    @Column(name = "month")
    private Integer month;          // 1-12 for monthly budgets

    @Column(name = "budget_date")
    private LocalDate budgetDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
