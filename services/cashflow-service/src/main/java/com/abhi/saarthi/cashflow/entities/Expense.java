package com.abhi.saarthi.cashflow.entities;

import com.abhi.saarthi.cashflow.constants.ExpenseFor;
import com.abhi.saarthi.cashflow.constants.ExpenseType;
import com.abhi.saarthi.cashflow.constants.PaymentMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
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

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ExpenseType type = ExpenseType.ONE_TIME; // ONE_TIME, RECURRING

    @Column(name = "created_date")
    @Setter(AccessLevel.PRIVATE)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_for")
    private ExpenseFor expenseFor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expense_items", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "item_name", length = 255)
    @Builder.Default
    private List<String> items = new ArrayList<>();
}
