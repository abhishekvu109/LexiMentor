package com.abhi.flashcard.entity;

import com.abhi.flashcard.entity.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "content_blocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // External-facing business key
    @Column(name = "ref_id", nullable = false, unique = true, updatable = false, length = 36)
    private String refId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ContentType contentType;

    // HTML block: raw HTML string; IMAGE block: URL or data URI; CODE block: source code;
    // LATEX block: LaTeX expression; AUDIO/VIDEO block: media URL
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // JSON metadata per type: IMAGE → {"mimeType":"image/png","altText":"..."};
    //                         CODE  → {"language":"python","filename":"sol.py"}
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @PrePersist
    private void assignRefId() {
        if (this.refId == null) {
            this.refId = UUID.randomUUID().toString();
        }
    }
}
