package com.abhi.saarthi.cashflow.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;            // "Groceries", "Rent", "Entertainment"

    @Column(name = "status")
    private int status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
