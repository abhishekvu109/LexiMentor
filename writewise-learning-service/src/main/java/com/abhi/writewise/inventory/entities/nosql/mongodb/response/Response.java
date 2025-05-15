package com.abhi.writewise.inventory.entities.nosql.mongodb.response;

import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.Evaluation;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import lombok.*;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {
    private long refId;
    private Topic topic;
    private String uuid;
    private String response;
    private int responseStatus;
    private Evaluation evaluation;
}
