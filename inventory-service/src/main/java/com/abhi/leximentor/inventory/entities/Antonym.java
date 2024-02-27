package com.abhi.leximentor.inventory.entities;

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
@Table(name = "antonym")
public class Antonym {
    @Id
    @Column(name = "antonym_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "b_key")
    private String key;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private WordMetadata wordId;

    @Column(name = "antonym_word_id")
    private long antonymWordId;

    @Column(name = "antonym")
    private String antonym;

    @Column(name = "crtn_date")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime crtnDate;

    @Column(name = "last_upd_date")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastUpdDate;
}
