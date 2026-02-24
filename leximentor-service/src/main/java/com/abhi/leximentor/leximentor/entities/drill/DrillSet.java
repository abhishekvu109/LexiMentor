package com.abhi.leximentor.leximentor.entities.drill;

import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "drill_set")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"drill","word"})
public class DrillSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @ManyToOne
    @JoinColumn(name = "drill_id")
    private Drill drill;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private WordMetadata word;
}
