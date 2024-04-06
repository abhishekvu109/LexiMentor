package com.abhi.synapster.inventory.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class ContentDTO {
    private String contentRefId;
    private String mainHeading;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
    private String status;
    private TopicDTO topicDTO;
    private String content;
    private List<DocumentDTO> documentDTOList;
}
