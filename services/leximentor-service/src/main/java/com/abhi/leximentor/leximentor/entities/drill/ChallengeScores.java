package com.abhi.leximentor.leximentor.entities.drill;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"challenge","drillSet"})
@Entity
@Table(name = "challenge_score")
public class ChallengeScores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "`key`")
    private String key;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "drill_set_id")
    private DrillSet drillSet;

    @Column(name = "is_correct")
    private boolean isCorrect;

    @Column(name = "question")
    private String question;

    @Column(name = "response")
    private String response;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "description", length = 5000)
    private String description;
}

