package com.abhi.flashcard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flashcard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    // Stored as HTML — preserves rich content including embedded <img> tags
    @Column(name = "question", columnDefinition = "TEXT", nullable = false)
    private String question;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<ContentBlock> answerBlocks = new ArrayList<>();

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @ElementCollection
    @CollectionTable(name = "flashcard_tags", joinColumns = @JoinColumn(name = "flashcard_id"))
    @Column(name = "tag", length = 100)
    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
