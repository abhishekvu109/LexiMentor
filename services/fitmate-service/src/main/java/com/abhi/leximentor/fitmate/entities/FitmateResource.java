package com.abhi.leximentor.fitmate.entities;

import com.abhi.leximentor.fitmate.constants.ResourceType;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@Entity
@ToString
@Table(name = "fitmate_resource")
public class FitmateResource {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "ref_id", unique = true)
    private long refId;

    @Column(name = "type", unique = false)
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @Column(name = "file_name", unique = false)
    private String fileName;

    @Column(name = "file_path", unique = false)
    private String filePath;

    @Column(name = "extension", unique = false)
    private String extension;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "placeholder", nullable = true)
    private String placeholder;
}
