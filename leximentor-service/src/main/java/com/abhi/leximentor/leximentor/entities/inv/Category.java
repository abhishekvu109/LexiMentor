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
@ToString
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "key")
    private String key;

    @Column(name = "source")
    private String source;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private WordMetadata word;

    @Column(name = "category")
    private String category;

    @Column(name = "created_at")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;
}
