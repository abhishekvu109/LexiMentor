package com.abhi.leximentor.leximentor.entities.drill;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"drillId", "drillChallengeScoresList"})
@Entity
@Table(name = "challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private long id;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "drill_id")
    private DrillMetadata drillId;

    @Column(name = "score")
    private double score;

    @Column(name = "is_pass")
    private boolean isPass;

    @Column(name = "total_correct")
    private int totalCorrect;

    @Column(name = "total_wrong")
    private int totalWrong;

    @Column(name = "crtn_date")
    @CreationTimestamp
    private LocalDateTime crtnDate;

    @OneToMany(mappedBy = "challengeId", cascade = CascadeType.ALL)
    private List<ChallengeScores> challengeScoresList;

    @Column(name = "challenge_type")
    private String challengeType;

    @Column(name = "status")
    private int status;

    @Column(name = "evaluation_status")
    private int evaluationStatus;

    @Column(name = "evaluation_job_id")
    private String evaluationJobId;

    @Column(name = "username")
    private String username;
}
