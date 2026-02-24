package com.abhi.leximentor.leximentor.entities.inv;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
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
@Table(name = "evaluator")
public class Evaluator {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "created_at")
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private int status;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type")
    private ChallengeType challengeType;
}
