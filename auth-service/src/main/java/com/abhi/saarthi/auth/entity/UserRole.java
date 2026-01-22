package com.abhi.saarthi.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @Column(unique = true, nullable = false)
    private String name;          // "fitmate_user", "cashflow_viewer", "premium", "admin"

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private int status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
