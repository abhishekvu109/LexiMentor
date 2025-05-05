package com.abhi.leximentor.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "named_object")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NamedObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_id", unique = true, nullable = false)
    private long refId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String genre;  // E.g., "Mythology", "Astronomy", "Nature"

    @Column(nullable = true, length = 50)
    private String subGenre;  // E.g., "Mythology", "Astronomy", "Nature"

    @Column(length = 255)
    private String description;

    @Column(length = 100)
    private String alias;

    @Column(name = "status")
    private int status;

    @Column(length = 100,name="tags")
    private String tags; // Comma-separated tags like "Greek,Warrior,Olympus"

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime updatedAt;
}
