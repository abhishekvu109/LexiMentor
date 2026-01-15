package com.abhi.saarthi.cashflow.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long refId;

    private String uuid;

    @Column(nullable = false)
    private String name;            // "Groceries", "Rent", "Entertainment"

    private int status;

    private LocalDateTime createdAt = LocalDateTime.now();
}
