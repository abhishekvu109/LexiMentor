package com.abhi.writewise.inventory.entities.nosql.mongodb.topic;

import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "llm_topic")
public class TopicGeneration {
    @Id
    private ObjectId id;
    private long refId;
    private String uuid;
    private String subject;
    private int numOfTopic;
    private String purpose;
    private int wordCount;
    private String prompt;
    private List<Topic> topics;
    private List<String> recommendations;
}
