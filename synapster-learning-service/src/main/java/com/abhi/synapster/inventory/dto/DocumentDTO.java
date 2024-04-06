package com.abhi.synapster.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentDTO {
    private String documentRefId;
    private String name;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
    private String status;
    private String documentLocation;
    private String storageType;
    private String type;
    private ContentDTO contentDTO;
}
