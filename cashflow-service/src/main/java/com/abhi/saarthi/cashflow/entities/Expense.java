package com.abhi.saarthi.cashflow.entities;

import com.abhi.saarthi.cashflow.constants.ExpenseType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long refId;

    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private ExpenseType type = ExpenseType.ONE_TIME; // ONE_TIME, RECURRING

    @Column(name = "created_date")
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedAt;
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
