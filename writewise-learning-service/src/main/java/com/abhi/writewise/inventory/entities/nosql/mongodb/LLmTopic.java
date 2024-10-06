package com.abhi.writewise.inventory.entities.nosql.mongodb;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "llm_topic")
public class LLmTopic {
    @Id
    private Long id;
    private String subject;
    private int numOfTopic;
    private String purpose;
    private int wordCount;
    private String prompt;
    private List<Topic> topics;
    private List<String> recommendations;
}
