package com.abhi.synapster.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubjectDTO {
    private String subjectRefId;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
    private String name;
    private String status;
    private String description;
    private String category;
}
