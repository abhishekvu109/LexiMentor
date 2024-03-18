package com.abhi.leximentor.inventory.entities.drill;

import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "drill_evaluation")
public class DrillEvaluation {

    @Id
    @Column(name = "drill_evaluation_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "ref_id", unique = true, nullable = false)
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "drill_challenge_score_id")
    private DrillChallengeScores drillChallengeScores;

    @ManyToOne
    @JoinColumn(name = "drill_evaluator")
    private Evaluator evaluator;

    @Column(name = "confidence")
    private double confidence;

    @Column(name = "evaluation_time")
    private double evaluationTime;

    @Column(name = "reason", length = 5000)
    private String reason;
}
