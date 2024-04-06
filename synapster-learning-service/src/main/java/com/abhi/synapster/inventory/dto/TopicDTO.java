package com.abhi.synapster.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TopicDTO {
    private String topicRefId;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
    private String name;
    private String status;
    private String description;
    private SubjectDTO subjectDTO;
    private String parentTopic;
}
