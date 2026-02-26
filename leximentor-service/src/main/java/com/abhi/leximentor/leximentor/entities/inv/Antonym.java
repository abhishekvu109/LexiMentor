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
@Table(name = "antonym")
public class Antonym {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "`key`",unique = true)
    private String key;

    @Column(name = "source")
    private String source;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private WordMetadata word;

    @Column(name = "antonym")
    private String antonym;

    @Column(name = "created_at")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;
}

