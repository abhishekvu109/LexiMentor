package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationDTO;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationMetricDTO;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationResultDTO;
import com.abhi.writewise.inventory.dto.response.ResponseDTO;
import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;
import com.abhi.writewise.inventory.dto.response.ResponseVersionDTO;
import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.Evaluation;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationMetric;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationResult;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseVersion;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import com.abhi.writewise.inventory.entities.sql.mysql.WritingSession;
import com.abhi.writewise.inventory.util.KeyGeneratorUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;

public class TopicResponseEvalServiceUtil {
    public static class TopicUtil {
        public static TopicGeneration buildEntity(TopicGenerationDTO dto) {
            return TopicGeneration.builder()
                    .subject(dto.getSubject())
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .numOfTopic(dto.getNumOfTopic())
                    .purpose(dto.getPurpose())
                    .wordCount(dto.getWordCount())
                    .prompt(dto.getPrompt())
                    .recommendations(dto.getRecommendations())
                    .topics((CollectionUtils.isNotEmpty(dto.getTopics())?dto.getTopics().stream().map(TopicUtil::buildEntity).toList():Collections.emptyList()))
                    .build();
        }

        public static Topic buildEntity(TopicDTO dto) {
            return Topic.builder()
                    .topicNo(dto.getTopicNo())
                    .topic(dto.getTopic())
                    .subject(dto.getSubject())
                    .description(dto.getDescriptions())
                    .points(dto.getPoints())
                    .learning(dto.getLearning())
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .build();
        }

        public static TopicGenerationDTO buildLlmTopicDTO(WritingSession sqlEntity, TopicGeneration noSqlEntity) {
            return TopicGenerationDTO.builder()
                    .subject(noSqlEntity.getSubject())
                    .numOfTopic(noSqlEntity.getNumOfTopic())
                    .purpose(noSqlEntity.getPurpose())
                    .wordCount(noSqlEntity.getWordCount())
                    .prompt(noSqlEntity.getPrompt())
                    .topics((CollectionUtils.isNotEmpty(noSqlEntity.getTopics())?noSqlEntity.getTopics().stream().map(TopicUtil::buildTopicDTO).toList():Collections.emptyList()))
                    .recommendations(noSqlEntity.getRecommendations())
                    .status(Status.Topic.getStatusStr(sqlEntity.getStatus()))
                    .refId(String.valueOf(sqlEntity.getRefId()))
                    .uuid(sqlEntity.getUuid())
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
                    .refId(String.valueOf(topic.getRefId()))
                    .uuid(topic.getUuid())
                    .build();
        }
    }

    public static class ResponseUtil {

        public static class BuildDTO {
            public static ResponseDTO buildResponse(Response entity) {
                return ResponseDTO.builder()
                        .refId(String.valueOf(entity.getRefId()))
                        .topic(TopicUtil.buildTopicDTO(entity.getTopic()))
                        .responseVersionDTOs((CollectionUtils.isNotEmpty(entity.getResponseVersions())?entity.getResponseVersions().stream().map(BuildDTO::buildResponseVersion).toList():Collections.emptyList()))
                        .build();
            }

            public static ResponseMasterDTO buildResponseMaster(ResponseMaster entity) {
                return ResponseMasterDTO.builder()
                        .topicGenerationRefId(entity.getTopicGenerationRefId())
                        .refId(String.valueOf(entity.getRefId()))
                        .uuid(entity.getUuid())
                        .status(Status.TopicResponse.getString(entity.getStatus()))
                        .score(entity.getScore())
                        .isPassed(entity.isPassed())
                        .createDate(entity.getCreateDate())
                        .lastUpdDate(entity.getLastUpdDate())
                        .topicResponseList((CollectionUtils.isNotEmpty(entity.getTopicResponseList())?entity.getTopicResponseList().stream().map(BuildDTO::buildResponse).toList():Collections.emptyList()))
                        .build();
            }

            public static ResponseVersionDTO buildResponseVersion(ResponseVersion entity){
                return ResponseVersionDTO.builder()
                        .refId(String.valueOf(entity.getRefId()))
                        .uuid(entity.getUuid())
                        .versionNumber(entity.getVersionNumber())
                        .createDate(entity.getCreateDate())
                        .lastUpdDate(entity.getLastUpdDate())
                        .isLatest(entity.isLatest())
                        .response(entity.getResponse())
                        .responseStatus(Status.TopicResponse.getString(entity.getResponseStatus()))
                        .evaluation(EvaluationUtil.BuildDTO.buildEvaluation(entity.getEvaluation()))
                        .build();
            }
        }

        public static class BuildEntity {
            public static Response buildResponse(ResponseDTO dto) {
                return Response.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .responseVersions(Collections.emptyList())
                        .build();
            }

            public static Response buildResponse() {
                return Response.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .responseVersions(Collections.emptyList())
                        .build();
            }

            public static ResponseMaster buildResponseMaster(String topicGenerationRefId, long sqlRefId) {
                return ResponseMaster.builder()
                        .topicGenerationRefId(topicGenerationRefId)
                        .sqlRefId(sqlRefId)
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .status(Status.TopicResponse.IN_PROGRESS)
                        .createDate(LocalDateTime.now())
                        .lastUpdDate(LocalDateTime.now())
                        .build();
            }

            public static ResponseMaster buildResponseMaster() {
                return ResponseMaster.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .status(Status.TopicResponse.NOT_STARTED)
                        .createDate(LocalDateTime.now())
                        .lastUpdDate(LocalDateTime.now())
                        .build();
            }

            public static ResponseVersion buildResponseVersion() {
                return ResponseVersion.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .isLatest(true)
                        .versionNumber(1)
                        .status(ApplicationConstants.Status.ACTIVE)
                        .createDate(LocalDateTime.now())
                        .lastUpdDate(LocalDateTime.now())
                        .evaluation(TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluation())
                        .build();
            }

            public static ResponseVersion buildResponseVersion(ResponseVersionDTO dto) {
                return ResponseVersion.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .createDate(LocalDateTime.now())
                        .lastUpdDate(LocalDateTime.now())
                        .evaluation(TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluation(dto.getEvaluation()))
                        .build();
            }
        }
    }


    public static class EvaluationUtil {

        public static class BuildDTO {
            public static EvaluationMetricDTO buildEvaluationMetric(EvaluationMetric entity) {
                return EvaluationMetricDTO.builder()
                        .refId(String.valueOf(entity.getRefId()))
                        .uuid(entity.getUuid())
                        .category(ApplicationConstants.EvaluationCategory.getString(entity.getCategory()))
                        .score(entity.getScore())
                        .comments(entity.getComments())
                        .alternateSuggestions(entity.getAlternateSuggestions())
                        .build();
            }

            public static EvaluationResultDTO buildEvaluationResult(EvaluationResult entity) {
                return EvaluationResultDTO.builder()
                        .refId(String.valueOf(entity.getRefId()))
                        .uuid(entity.getUuid())
                        .spelling(BuildDTO.buildEvaluationMetric(entity.getSpelling()))
                        .grammar(BuildDTO.buildEvaluationMetric(entity.getGrammar()))
                        .punctuation(BuildDTO.buildEvaluationMetric(entity.getPunctuation()))
                        .vocabulary(BuildDTO.buildEvaluationMetric(entity.getVocabulary()))
                        .styleAndTone(BuildDTO.buildEvaluationMetric(entity.getStyleAndTone()))
                        .creativityAndThinking(BuildDTO.buildEvaluationMetric(entity.getCreativityAndThinking()))
                        .score(entity.getScore())
                        .comments(entity.getOverallRecommendations())
                        .build();
            }

            public static EvaluationDTO buildEvaluation(Evaluation entity) {
                return EvaluationDTO.builder()
                        .refId(String.valueOf(entity.getRefId()))
                        .uuid(entity.getUuid())
                        .evaluationResult(BuildDTO.buildEvaluationResult(entity.getEvaluationResult()))
                        .createDate(entity.getCreateDate())
                        .lastUpdDate(entity.getLastUpdDate())
                        .evaluationStatus(Status.EvaluationStatus.getString(entity.getEvaluationStatus()))
                        .score(entity.getScore())
                        .build();
            }
        }

        public static class BuildEntity {

            public static EvaluationMetric buildEvaluationMetric(EvaluationMetricDTO dto) {
                return EvaluationMetric.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .category(ApplicationConstants.EvaluationCategory.getInt(dto.getCategory()))
                        .score(dto.getScore())
                        .comments(dto.getComments())
                        .alternateSuggestions(dto.getAlternateSuggestions())
                        .build();
            }

            public static EvaluationMetric buildEvaluationMetric() {
                return EvaluationMetric.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .comments(Collections.emptyList())
                        .alternateSuggestions(Collections.emptyList())
                        .build();
            }

            public static EvaluationResult buildEvaluationResult(EvaluationResultDTO dto) {
                return EvaluationResult.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .spelling(BuildEntity.buildEvaluationMetric(dto.getSpelling()))
                        .grammar(BuildEntity.buildEvaluationMetric(dto.getGrammar()))
                        .punctuation(BuildEntity.buildEvaluationMetric(dto.getPunctuation()))
                        .vocabulary(BuildEntity.buildEvaluationMetric(dto.getVocabulary()))
                        .styleAndTone(BuildEntity.buildEvaluationMetric(dto.getStyleAndTone()))
                        .creativityAndThinking(BuildEntity.buildEvaluationMetric(dto.getCreativityAndThinking()))
                        .score(dto.getScore())
                        .OverallRecommendations(dto.getComments())
                        .build();
            }

            public static EvaluationResult buildEvaluationResult() {
                return EvaluationResult.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .spelling(EvaluationUtil.BuildEntity.buildEvaluationMetric())
                        .grammar(EvaluationUtil.BuildEntity.buildEvaluationMetric())
                        .punctuation(EvaluationUtil.BuildEntity.buildEvaluationMetric())
                        .vocabulary(EvaluationUtil.BuildEntity.buildEvaluationMetric())
                        .styleAndTone(EvaluationUtil.BuildEntity.buildEvaluationMetric())
                        .creativityAndThinking(EvaluationUtil.BuildEntity.buildEvaluationMetric())
                        .OverallRecommendations(Collections.emptyList())
                        .build();
            }

            public static Evaluation buildEvaluation(EvaluationDTO dto) {
                return Evaluation.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .evaluationResult(BuildEntity.buildEvaluationResult(dto.getEvaluationResult()))
                        .createDate(LocalDateTime.now())
                        .lastUpdDate(LocalDateTime.now())
                        .evaluationStatus(Status.EvaluationStatus.NOT_STARTED)
                        .score(dto.getScore())
                        .build();
            }

            public static Evaluation buildEvaluation() {
                return Evaluation.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .createDate(LocalDateTime.now())
                        .lastUpdDate(LocalDateTime.now())
                        .evaluationStatus(Status.EvaluationStatus.NOT_STARTED)
                        .evaluationResult(EvaluationUtil.BuildEntity.buildEvaluationResult())
                        .build();
            }
        }

    }

}
