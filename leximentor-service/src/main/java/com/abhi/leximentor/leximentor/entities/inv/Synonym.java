package com.abhi.leximentor.leximentor.entities.inv;

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
@ToString(exclude = {"word"})
@Entity
@Table(name = "synonym")
public class Synonym {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "`key`",unique = true)
    private String key;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private WordMetadata word;

    @Column(name = "source")
    private String source;

    @Column(name = "synonym")
    private String synonym;

    @Column(name = "created_at")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;
}

