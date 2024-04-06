package com.abhi.synapster.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "synapster_inv_document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private long id;

    @Column(name = "ref_id", unique = true)
    private long refId;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @Column(name = "status")
    private int status;

    @Column(name = "document_location")
    private String documentLocation;

    @Column(name = "storage_type")
    private String storageType;

    @Column(name = "type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;
}
