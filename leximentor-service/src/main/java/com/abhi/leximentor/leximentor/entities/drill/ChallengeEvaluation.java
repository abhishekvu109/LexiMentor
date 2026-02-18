package com.abhi.leximentor.leximentor.entities.drill;

import com.abhi.leximentor.leximentor.entities.inv.Evaluator;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString(exclude = {"evaluator", "drillChallengeScores"})
@Entity
@Table(name = "challenge_evaluation")
public class ChallengeEvaluation {

    @Id
    @Column(name = "challenge_evaluation_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "ref_id", unique = true, nullable = false)
    private long refId;

    @Column(name = "uuid")
    private String uuid;

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
}
