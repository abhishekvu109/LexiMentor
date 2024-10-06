package com.abhi.writewise.inventory.entities.nosql.mongodb;

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
@Document(collection = "topic")
public class Topic {
    @Id
    private ObjectId id;
    private int topicNo;
    private String topic;
    private String subject;
    private String description;
    private List<String> points;
    private String learning;
}
