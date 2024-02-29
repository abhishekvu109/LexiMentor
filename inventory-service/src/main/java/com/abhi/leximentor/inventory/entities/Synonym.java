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
@Table(name = "synonym")
public class Synonym {

    @Id
    @Column(name = "synonym_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "b_key")
    private String key;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private WordMetadata wordId;

    @Column(name = "synonym_word_id")
    private long synonymWordId;

    @Column(name = "source")
    private String source;

    @Column(name = "synonym")
    private String synonym;

    @Column(name = "crtn_date")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime crtnDate;

    @Column(name = "last_upd_date")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastUpdDate;
}
