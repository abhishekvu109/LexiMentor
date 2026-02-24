package com.abhi.leximentor.leximentor.entities.drill;

import com.abhi.leximentor.leximentor.entities.inv.Evaluator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString(exclude = {"evaluator", "challengeScores"})
@Entity
@Table(name = "challenge_evaluation")
public class ChallengeEvaluation {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;


    @Column(name = "key")
    private String key;

    @ManyToOne
    @JoinColumn(name = "challenge_score_id")
    private ChallengeScores challengeScores;

    @ManyToOne
    @JoinColumn(name = "challenge_evaluator")
    private Evaluator evaluator;

    @Column(name = "confidence")
    private double confidence;

    @Column(name = "evaluation_time")
    private double evaluationTime;

    @Column(name = "reason", length = 5000)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
