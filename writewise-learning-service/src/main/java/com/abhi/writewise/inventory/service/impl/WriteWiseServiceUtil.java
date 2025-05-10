package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.LlmTopicDTO;
import com.abhi.writewise.inventory.dto.TopicDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.LLmTopic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.Topic;
import com.abhi.writewise.inventory.entities.sql.mysql.LLmTopicMaster;

public class WriteWiseServiceUtil {
    public static class TopicServiceUtil {
        public static LLmTopic buildEntity(LlmTopicDTO dto) {
            return LLmTopic.builder().subject(dto.getSubject()).numOfTopic(dto.getNumOfTopic()).purpose(dto.getPurpose()).wordCount(dto.getWordCount()).prompt(dto.getPrompt()).recommendations(dto.getRecommendations()).topics(dto.getTopics().stream().map(TopicServiceUtil::buildEntity).toList()).build();
        }

        public static Topic buildEntity(TopicDTO dto) {
            return Topic.builder().topicNo(dto.getTopicNo()).topic(dto.getTopic()).subject(dto.getSubject()).description(dto.getDescriptions()).points(dto.getPoints()).learning(dto.getLearning()).build();
        }

        public static LlmTopicDTO buildLlmTopicDTO(LLmTopicMaster sqlEntity, LLmTopic noSqlEntity) {
            return LlmTopicDTO.builder()
                    .subject(noSqlEntity.getSubject())
                    .numOfTopic(noSqlEntity.getNumOfTopic())
                    .purpose(noSqlEntity.getPurpose())
                    .wordCount(noSqlEntity.getWordCount())
                    .prompt(noSqlEntity.getPrompt())
                    .topics(noSqlEntity.getTopics().stream().map(TopicServiceUtil::buildTopicDTO).toList())
                    .recommendations(noSqlEntity.getRecommendations())
                    .status(Status.Topic.getStatusStr(sqlEntity.getStatus()))
                    .build();
        }

        public static TopicDTO buildTopicDTO(Topic topic) {
            return TopicDTO.builder()
                    .topicNo(topic.getTopicNo())
                    .topic(topic.getTopic())
                    .subject(topic.getSubject())
                    .descriptions(topic.getDescription())
                    .points(topic.getPoints())
                    .learning(topic.getLearning())
                    .build();
        }
    }
}
