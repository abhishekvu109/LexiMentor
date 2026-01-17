package com.abhi.saarthi.cashflow.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    private LocalDateTime createdAt;
}
