package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationDTO;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationErrorListDTO;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationMetricDTO;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationResultDTO;
import com.abhi.writewise.inventory.dto.response.ResponseDTO;
import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;
import com.abhi.writewise.inventory.dto.response.ResponseVersionDTO;
import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.Evaluation;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationErrorList;
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
import java.util.Comparator;

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
                    .topics((CollectionUtils.isNotEmpty(dto.getTopics()) ? dto.getTopics().stream().map(TopicUtil::buildEntity).toList() : Collections.emptyList()))
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

        public static TopicGenerationDTO buildDTO(WritingSession sqlEntity, TopicGeneration noSqlEntity) {
            return TopicGenerationDTO.builder()
                    .subject(noSqlEntity.getSubject())
                    .writingSessionRefId(String.valueOf(sqlEntity.getRefId()))
                    .numOfTopic(noSqlEntity.getNumOfTopic())
                    .purpose(noSqlEntity.getPurpose())
                    .wordCount(noSqlEntity.getWordCount())
                    .prompt(noSqlEntity.getPrompt())
                    .topics((CollectionUtils.isNotEmpty(noSqlEntity.getTopics()) ? noSqlEntity.getTopics().stream().map(TopicUtil::buildDTO).toList() : Collections.emptyList()))
                    .recommendations(noSqlEntity.getRecommendations())
                    .status(Status.Topic.getStatusStr(sqlEntity.getStatus()))
                    .refId(String.valueOf(noSqlEntity.getRefId()))
                    .uuid(noSqlEntity.getUuid())
                    .build();
        }

        public static TopicDTO buildDTO(Topic topic) {
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
                        .uuid(entity.getUuid())
                        .topic(TopicUtil.buildDTO(entity.getTopic()))
                        .responseVersionDTOs((CollectionUtils.isNotEmpty(entity.getResponseVersions()) ? entity.getResponseVersions().stream().map(BuildDTO::buildResponseVersion).sorted(Comparator.comparing(ResponseVersionDTO::getVersionNumber).reversed()).toList() : Collections.emptyList()))
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
                        .topicResponseList((CollectionUtils.isNotEmpty(entity.getTopicResponseList()) ? entity.getTopicResponseList().stream().map(BuildDTO::buildResponse).toList() : Collections.emptyList()))
                        .build();
            }

            public static ResponseVersionDTO buildResponseVersion(ResponseVersion entity) {
                return ResponseVersionDTO.builder()
                        .refId(String.valueOf(entity.getRefId()))
                        .uuid(entity.getUuid())
                        .versionNumber(entity.getVersionNumber())
                        .createDate(entity.getCreateDate())
                        .lastUpdDate(entity.getLastUpdDate())
                        .isLatest(entity.isLatest())
                        .response(entity.getResponse())
                        .status(ApplicationConstants.Status.getStatusStr(entity.getStatus()))
                        .responseStatus(Status.TopicResponse.getString(entity.getResponseStatus()))
                        .evaluation((entity.getEvaluation()!=null)?EvaluationUtil.BuildDTO.buildEvaluation(entity.getEvaluation()):null)
                        .evaluations(CollectionUtils.isNotEmpty(entity.getEvaluations()) ? entity.getEvaluations().stream().map(EvaluationUtil.BuildDTO::buildEvaluation).toList() : Collections.emptyList())
                        .llmEvaluationText(entity.getLlmEvaluationText())
                        .llmEvaluationStatus(Status.EvaluationStatus.getString(entity.getLlmEvaluationStatus()))
                        .lowLevelEvaluationText(entity.getLowLevelEvaluationText())
                        .lowLevelEvaluationStatus(Status.EvaluationStatus.getString(entity.getLowLevelEvaluationStatus()))
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
                        .evaluations(Collections.emptyList())
                        .llmEvaluationStatus(Status.EvaluationStatus.NOT_STARTED)
                        .build();
            }

            public static ResponseVersion buildResponseVersion(ResponseVersionDTO dto) {
                return ResponseVersion.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .createDate(LocalDateTime.now())
                        .lastUpdDate(LocalDateTime.now())
                        .llmEvaluationStatus(Status.EvaluationStatus.NOT_STARTED)
                        .evaluation(TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluation(dto.getEvaluation()))
                        .evaluations(Collections.emptyList())
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
                        .alternateSuggestion(entity.getAlternateSuggestion())
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
                        .evaluator(entity.getEvaluator())
                        .evaluationResult(BuildDTO.buildEvaluationResult(entity.getEvaluationResult()))
                        .createDate(entity.getCreateDate())
                        .lastUpdDate(entity.getLastUpdDate())
                        .evaluationStatus(Status.EvaluationStatus.getString(entity.getEvaluationStatus()))
                        .score(entity.getScore())
                        .errorList(CollectionUtils.isNotEmpty(entity.getErrorList()) ? entity.getErrorList().stream().map(TopicResponseEvalServiceUtil.EvaluationUtil.BuildDTO::buildErrorList).toList() : Collections.emptyList())
                        .build();
            }

            public static EvaluationErrorListDTO buildErrorList(EvaluationErrorList entity) {
                return EvaluationErrorListDTO.builder()
                        .refId(entity.getRefId())
                        .uuid(entity.getUuid())
                        .id(entity.getId())
                        .start(entity.getStart())
                        .end(entity.getEnd())
                        .type(entity.getType())
                        .subType(entity.getSubType())
                        .incorrectText(entity.getIncorrectText())
                        .correctText(entity.getCorrectedText())
                        .explanation(entity.getExplanation())
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
                        .alternateSuggestion(dto.getAlternateSuggestion())
                        .build();
            }

            public static EvaluationMetric buildEvaluationMetric() {
                return EvaluationMetric.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .uuid(KeyGeneratorUtil.uuid())
                        .comments(Collections.emptyList())
                        .alternateSuggestion(Collections.emptyList())
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
                        .evaluator(dto.getEvaluator())
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

            public static Evaluation buildEvaluation(String evaluatorName) {
                return Evaluation.builder()
                        .refId(KeyGeneratorUtil.refId())
                        .evaluator(evaluatorName)
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
