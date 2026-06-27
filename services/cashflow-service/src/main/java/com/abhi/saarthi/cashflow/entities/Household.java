package com.abhi.saarthi.cashflow.entities;

import com.abhi.saarthi.cashflow.constants.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "household")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name",nullable = false)
    private String name;            // "Abhishek & Priya Home", "The Smith Family", etc.

    @Enumerated
    @Column(name = "currency")
    private Currency currency;        // default: "INR"

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseholdMember> members;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deposit> deposits;

    @Column(name = "status")
    private int status;
}
