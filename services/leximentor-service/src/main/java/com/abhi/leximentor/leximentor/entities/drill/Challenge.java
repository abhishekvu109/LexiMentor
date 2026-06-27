package com.abhi.leximentor.leximentor.entities.drill;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"drill", "challengeScoresList"})
@Entity
@Table(name = "challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "`key`",unique = true)
    private String key;

    @ManyToOne
    @JoinColumn(name = "drill_id")
    private Drill drill;

    @Column(name = "score")
    private double score;

    @Column(name = "is_pass")
    private boolean isPass;

    @Column(name = "total_correct")
    private int totalCorrect;

    @Column(name = "total_wrong")
    private int totalWrong;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<ChallengeScores> challengeScoresList;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type")
    private ChallengeType challengeType;

    @Column(name = "status")
    private int status;

    @Column(name = "evaluation_status")
    private int evaluationStatus;

    @Column(name = "evaluation_job_id")
    private String evaluationJobId;

    @Column(name = "username")
    private String username;
}

