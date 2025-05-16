package com.abhi.writewise.inventory.entities.nosql.mongodb.response;

import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "writewise_llm_response_evaluation_master")
public class ResponseMaster {

    @Id
    private ObjectId id;
    private String topicGenerationRefId;
    private long sqlRefId;
    private long refId;
    private String uuid;
    private int status;
    private double score;
    private boolean isPassed;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdDate;
    private List<Response> topicResponseList;
}
