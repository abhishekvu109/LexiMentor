package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.ModelConstants;
import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.constants.StringConstants;
import com.abhi.writewise.inventory.dto.response.ResponseVersionDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.Evaluation;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationErrorList;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationResult;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseVersion;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.model.PromptRequest;
import com.abhi.writewise.inventory.model.PromptResponse;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.impl.RepositoryUtil;
import com.abhi.writewise.inventory.service.LLMService;
import com.abhi.writewise.inventory.service.ResponseVersionService;
import com.abhi.writewise.inventory.util.KeyGeneratorUtil;
import com.abhi.writewise.inventory.util.LLMPromptBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResponseVersionServiceImpl implements ResponseVersionService {
    private final ResponseMasterRepository responseMasterRepository;
    private final LLMService llmService;
    private final RepositoryUtil repositoryUtil;

    @Getter
    private String model;
    private String prompt;

    @Override
    public ResponseVersionDTO getResponseVersion(long topicRefId, long versionRefId) {
        return TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO.buildResponseVersion(repositoryUtil.findResponseVersionByTopicRefIdAndVersionRefId(topicRefId, versionRefId));
    }

    @Override
    public String getPrompt(long topicRefId, long versionRefId, boolean isHighLevel) {
        ResponseVersion responseVersion = repositoryUtil.findResponseVersionByTopicRefIdAndVersionRefId(topicRefId, versionRefId);
        Topic topic = repositoryUtil.findTopicByTopicRefId(topicRefId);
        TopicGeneration topicGeneration = repositoryUtil.findTopicGenerationByTopicRefId(topicRefId);
        String mergedPoints = StringUtils.join(topic.getPoints().stream().map(point -> "\"" + point + "\"").toList(), ",");
        String mergedRecommendations = StringUtils.join(topicGeneration.getRecommendations().stream().map(recommend -> "\"" + recommend + "\"").toList(), ",");

        String trimmedPoints = mergedPoints.endsWith(",") ? mergedPoints.substring(0, mergedPoints.length() - 1) : mergedPoints;
        String trimmedRecommendations = mergedRecommendations.endsWith(",") ? mergedRecommendations.substring(0, mergedRecommendations.length() - 1) : mergedRecommendations;

        return isHighLevel ? LLMPromptBuilder.EvaluationPrompt.getHighLevelEvaluationPrompt(topic.getTopic(), topic.getSubject(), trimmedPoints, trimmedRecommendations, responseVersion.getResponse()) : LLMPromptBuilder.EvaluationPrompt.getLowLevelEvaluationPrompt(topic.getTopic(), topic.getSubject(), trimmedPoints, responseVersion.getResponse());
    }

    @Override
    @Transactional
    public String doEvaluationForTopicAndVersion(long topicRefId, long versionRefId, boolean isHighLevel) {
        ResponseMaster responseMaster = repositoryUtil.findResponseMasterByTopicRefId(topicRefId);
        ResponseVersion responseVersion = repositoryUtil.findResponseVersionByTopicRefIdAndVersionRefId(responseMaster, topicRefId, versionRefId);
        CompletableFuture.runAsync(() -> {
            if (isHighLevel) {
                responseVersion.setLlmEvaluationStatus(Status.EvaluationStatus.IN_PROGRESS);
            } else {
                responseVersion.setLowLevelEvaluationStatus(Status.EvaluationStatus.IN_PROGRESS);
            }
            responseMasterRepository.save(responseMaster);
        });
        PromptRequest promptRequest = PromptRequest.builder().format(isHighLevel ? StringConstants.LLM_EVALUATION_HIGH_LEVEL : StringConstants.LLM_EVALUATION_LOW_LEVEL).model(StringUtils.isNotEmpty(this.model) ? this.model : ModelConstants.CLOUD_LLM).prompt(StringUtils.isNotEmpty(this.prompt) ? prompt : "").build();
        if (StringUtils.isEmpty(promptRequest.getPrompt())) {
            promptRequest.setPrompt(getPrompt(topicRefId, versionRefId, isHighLevel));
        }
        promptRequest.setOptions(ApplicationConstants.DEFAULT_OLLAMA_OPTIONS);
        int RETRY_COUNT = 3;
        PromptResponse promptResponse = null;
        while (RETRY_COUNT > 0) {
            try {
                promptResponse = llmService.execute(promptRequest);
                break;
            } catch (ServerException.InternalError ex) {
                RETRY_COUNT--;
            }
        }
        if (promptResponse == null || StringUtils.isEmpty(promptResponse.getResponse())) {
            throw new ServerException().new InternalError("LLM Response is null");
        }

        if (isHighLevel) {
            responseVersion.setLlmEvaluationStatus(Status.EvaluationStatus.COMPLETED);
            responseVersion.setLlmEvaluationText(promptResponse.getResponse());
            responseVersion.setLowLevelEvaluationModel(this.getModel());
        } else {
            responseVersion.setLowLevelEvaluationStatus(Status.EvaluationStatus.COMPLETED);
            responseVersion.setLowLevelEvaluationText(promptResponse.getResponse());
            responseVersion.setHighLevelEvaluationModel(this.getModel());
        }

        responseMasterRepository.save(responseMaster);
        return promptResponse.getResponse();
    }

    @Override
    public void setModelName(String model) {
        this.model = model;
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    @Transactional
    public ResponseVersionDTO doSubmitResult(long topicRefId, long versionRefId, boolean isHighLevel) {
        ResponseMaster responseMaster = repositoryUtil.findResponseMasterByTopicRefId(topicRefId);
        ResponseVersion responseVersion = repositoryUtil.findResponseVersionByTopicRefIdAndVersionRefId(responseMaster, topicRefId, versionRefId);
        Evaluation evaluation = responseVersion.getEvaluation();
        if (evaluation == null) {
            evaluation = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluation(this.model);
        }
        if (isHighLevel) {
            String highLevelResult = responseVersion.getLlmEvaluationText();
            if (StringUtils.isEmpty(highLevelResult)) {
                throw new ServerException().new InternalError("Please generate the high-level evaluation results.");
            }
            EvaluationResult evaluationResult = ResponseVersionUtil.fromJsonStrToEvaluationResult(highLevelResult);
            evaluationResult.setRefId(KeyGeneratorUtil.refId());
            evaluationResult.setUuid(KeyGeneratorUtil.uuid());
            evaluationResult.getGrammar().setRefId(KeyGeneratorUtil.refId());
            evaluationResult.getVocabulary().setRefId(KeyGeneratorUtil.refId());
            evaluationResult.getSpelling().setRefId(KeyGeneratorUtil.refId());
            evaluationResult.getCreativityAndThinking().setRefId(KeyGeneratorUtil.refId());
            evaluationResult.getStyleAndTone().setRefId(KeyGeneratorUtil.refId());
            evaluationResult.getPunctuation().setRefId(KeyGeneratorUtil.refId());
            evaluation.setScore(getScore(evaluationResult));
            evaluation.setEvaluationResult(evaluationResult);
            Evaluation existingEvaluation = isExistingEvaluation(responseVersion);
            if (existingEvaluation != null) {
                existingEvaluation.setEvaluationResult(evaluationResult);
                existingEvaluation.setScore(getScore(evaluationResult));
                log.info("Overwrote existing high-level evaluation for model {}.", this.model);
            } else {
                evaluation.setEvaluator(this.model);
                List<Evaluation> evaluations = responseVersion.getEvaluations();
                if (CollectionUtils.isEmpty(evaluations)) {
                    evaluations = new ArrayList<>();
                }
                evaluations.add(evaluation);
                responseVersion.setEvaluations(evaluations);
                log.info("Added new high-level evaluation for model {}.", this.model);
            }
            responseVersion.setEvaluation(evaluation);
            responseVersion.setLlmEvaluationText("");
            responseVersion.setLlmEvaluationStatus(Status.EvaluationStatus.NOT_STARTED);
        } else {
            String lowLevelResult = responseVersion.getLowLevelEvaluationText();
            if (StringUtils.isEmpty(lowLevelResult)) {
                throw new ServerException().new InternalError("Please generate the low-level evaluation results.");
            }
            List<EvaluationErrorList> errorList = ResponseVersionUtil.fromJsonStrToEvaluation(lowLevelResult).getErrorList();
            ResponseVersionUtil.resolveOffsets(errorList, responseVersion.getResponse());
            evaluation.setErrorList(errorList);
            Evaluation existingEvaluation = isExistingEvaluation(responseVersion);
            if (existingEvaluation != null) {
                existingEvaluation.setErrorList(errorList);
                log.info("Overwrote existing low-level evaluation for model {}.", this.model);
            } else {
                evaluation.setEvaluator(this.model);
                List<Evaluation> evaluations = responseVersion.getEvaluations();
                if (CollectionUtils.isEmpty(evaluations)) {
                    evaluations = new ArrayList<>();
                }
                evaluations.add(evaluation);
                responseVersion.setEvaluations(evaluations);
                log.info("Added new low-level evaluation for model {}.", this.model);
            }
            responseVersion.setLowLevelEvaluationStatus(Status.EvaluationStatus.NOT_STARTED);
            responseVersion.setLowLevelEvaluationText("");
        }
        if (evaluation.getEvaluationResult() != null && CollectionUtils.isNotEmpty(evaluation.getErrorList())) {
            evaluation.setEvaluationStatus(Status.EvaluationStatus.COMPLETED);
            long evaluationRefId = evaluation.getRefId();
            if (CollectionUtils.isNotEmpty(responseVersion.getEvaluations())) {
                Evaluation evaluation1 = responseVersion.getEvaluations().stream().filter(eval -> eval.getRefId() == evaluationRefId).findAny().orElse(null);
                if (evaluation1 != null) {
                    evaluation1.setEvaluationStatus(Status.EvaluationStatus.COMPLETED);
                }
            }
        }
        responseMasterRepository.save(responseMaster);
        return getResponseVersion(topicRefId, versionRefId);
    }

    private double getScore(EvaluationResult result) {
        return result.getSpelling().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.SPELLING) + result.getPunctuation().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.PUNCTUATION) + result.getVocabulary().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.VOCABULARY) + result.getStyleAndTone().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.STYLE_AND_TONE) + result.getGrammar().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.GRAMMAR) + result.getCreativityAndThinking().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.CREATIVITY_AND_THINKING);
    }

    private Evaluation isExistingEvaluation(ResponseVersion responseVersion) {
        return CollectionUtils.isEmpty(responseVersion.getEvaluations()) ? null : responseVersion.getEvaluations().stream().filter(eval -> StringUtils.equalsIgnoreCase(eval.getEvaluator(), this.model)).findAny().orElse(null);
    }

    @Override
    public boolean doValidateEvaluationResult(long topicRefId, long versionRefId, boolean isHighLevel) {
        ResponseVersion responseVersion = repositoryUtil.findResponseVersionByTopicRefIdAndVersionRefId(topicRefId, versionRefId);
        String SCHEMA_STR = isHighLevel ? ApplicationConstants.HIGH_LEVEL_EVALUATION_RESULT_JSON_SCHEMA : ApplicationConstants.LOW_LEVEL_EVALUATION_RESULT_JSON_SCHEMA;
        String evaluationResult = isHighLevel ? responseVersion.getLlmEvaluationText() : responseVersion.getLowLevelEvaluationText();
        if (StringUtils.isAnyEmpty(SCHEMA_STR, evaluationResult)) {
            throw new ServerException().new InternalError("Schema | Evaluation Result is not generated.");
        }
        return ResponseVersionUtil.doValidateJsonSchema(evaluationResult, SCHEMA_STR);
    }

    @Override
    @Transactional
    public void doDeleteEvaluationAll(long topicRefId) {
        ResponseMaster responseMaster = repositoryUtil.findResponseMasterByTopicRefId(topicRefId);
        Response response = repositoryUtil.findResponseByTopicRefId(responseMaster, topicRefId);
        if (CollectionUtils.isNotEmpty(response.getResponseVersions())) {
            response.getResponseVersions().forEach(responseVersion -> {
                responseVersion.setEvaluation(TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluation());
                responseVersion.setEvaluations(Collections.emptyList());
                responseVersion.setLlmEvaluationText("");
                responseVersion.setLowLevelEvaluationText("");
                responseVersion.setLlmEvaluationStatus(Status.EvaluationStatus.NOT_STARTED);
                responseVersion.setLowLevelEvaluationStatus(Status.EvaluationStatus.NOT_STARTED);
            });
            responseMasterRepository.save(responseMaster);
        }
    }

    @Override
    @Transactional
    public void doDeleteEvaluationByVersion(long topicRefId, long versionRefId) {
        ResponseMaster responseMaster = repositoryUtil.findResponseMasterByTopicRefId(topicRefId);
        ResponseVersion responseVersion = repositoryUtil.findResponseVersionByTopicRefIdAndVersionRefId(responseMaster, topicRefId, versionRefId);
        responseVersion.setEvaluation(TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluation());
        responseVersion.setEvaluations(Collections.emptyList());
        responseVersion.setLlmEvaluationText("");
        responseVersion.setLowLevelEvaluationText("");
        responseVersion.setLlmEvaluationStatus(Status.EvaluationStatus.NOT_STARTED);
        responseVersion.setLowLevelEvaluationStatus(Status.EvaluationStatus.NOT_STARTED);
        responseMasterRepository.save(responseMaster);
    }

    @Override
    @Transactional
    public void deleteResponseVersion(long topicRefId, long versionRefId) {
        log.info("Deleting response version with topicRefId: {} and versionRefId: {}", topicRefId, versionRefId);
        ResponseMaster responseMaster = repositoryUtil.findResponseMasterByTopicRefId(topicRefId);
        Response response = repositoryUtil.findResponseByTopicRefId(responseMaster, topicRefId);

        ResponseVersion responseVersionToDelete = response.getResponseVersions().stream()
                .filter(rv -> rv.getRefId() == versionRefId)
                .findFirst()
                .orElseThrow(() -> new ServerException().new EntityObjectNotFound("ResponseVersion not found for refId: " + versionRefId));

        response.getResponseVersions().remove(responseVersionToDelete);
        responseMasterRepository.save(responseMaster);
        log.info("Successfully deleted response version with refId: {}", versionRefId);
    }
}

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ResponseVersionUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    public static EvaluationResult fromJsonStrToEvaluationResult(String object) {
        try {
//            String repaired = repairJson(object);
            String repaired = OllamaJsonRepairer.repairJson(object);
            EvaluationResult evaluationResult = MAPPER.readValue(repaired, EvaluationResult.class);
            evaluationResult.setRefId(KeyGeneratorUtil.refId());
            evaluationResult.setUuid(KeyGeneratorUtil.uuid());
            return evaluationResult;
        } catch (Exception e) {
            log.error("Failed to parse EvaluationResult. Raw response was:\n{}", object);
            log.error("Parse error: {}", e.getMessage());
            throw new ServerException().new InternalError("Unable to parse LLM response to EvaluationResult: " + e.getMessage());
        }
    }

    public static Evaluation fromJsonStrToEvaluation(String object) {
        try {
            String repaired = repairJson(object);
            return MAPPER.readValue(repaired, Evaluation.class);
        } catch (Exception e) {
            log.error("Failed to parse Evaluation. Raw response was:\n{}", object);
            log.error("Parse error: {}", e.getMessage());
            throw new ServerException().new InternalError("Unable to parse LLM response to Evaluation: " + e.getMessage());
        }
    }

    static String repairJson(String raw) {
        if (StringUtils.isBlank(raw)) return raw;
        String json = raw.trim();

        // FastAPI serialises a returned Python str as a JSON string: "{\n  \"key\": ...}"
        // Java's StringHttpMessageConverter stores it verbatim including the outer quotes
        // and escape sequences. Detect and unwrap that encoding first.
        if (json.startsWith("\"")) {
            try {
                json = MAPPER.readValue(json, String.class);
                json = json.trim();
            } catch (Exception e) {
                // fallback: strip outer quotes and unescape manually
                if (json.endsWith("\"")) json = json.substring(1, json.length() - 1);
                else json = json.substring(1);
                json = json.replace("\\n", "\n").replace("\\r", "").replace("\\\"", "\"").replace("\\\\", "\\").trim();
            }
        }

        // Strip markdown code fences the LLM sometimes wraps around JSON
        if (json.contains("```")) {
            json = json.replaceFirst("```(?:json)?\\s*", "").trim();
            if (json.contains("```")) json = json.substring(0, json.lastIndexOf("```")).trim();
        }

        // Strip markdown bold/italic formatting the LLM sometimes applies to field names
        // e.g. **"explanation"**: → "explanation":   or   *"type"*: → "type":
        json = json.replaceAll("\\*{1,2}(\"[^\"]+\")\\*{1,2}\\s*:", "$1:");

        // Fix string values where the LLM used \"...\" as delimiters instead of "..."
        // Step 1: opening \" right after , : or [ (value position) → just "
        json = json.replaceAll("([,:\\[]\\s*)\\\\\"", "$1\"");
        // Step 2: closing \" directly before , ] or newline → just "
        json = json.replaceAll("\\\\\"(?=\\s*[,\\]\\n])", "\"");

        // Skip any preamble text the LLM added before the JSON object
        int jsonStart = json.indexOf('{');
        if (jsonStart == -1) {
            log.error("repairJson: no JSON object found in LLM response: {}", json);
            return "{}";
        }
        if (jsonStart > 0) {
            log.warn("repairJson: skipping {} chars of preamble before JSON", jsonStart);
            json = json.substring(jsonStart);
        }

        // Walk the JSON tracking open brackets so we can close truncated output
        Deque<Character> stack = new ArrayDeque<>();
        boolean inString = false;
        int i = 0;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (inString) {
                if (c == '\\') { i += 2; continue; } // skip escaped char
                if (c == '"') inString = false;
            } else {
                switch (c) {
                    case '"' -> inString = true;
                    case '{' -> stack.push('}');
                    case '[' -> stack.push(']');
                    case '}', ']' -> { if (!stack.isEmpty()) stack.pop(); }
                }
            }
            i++;
        }

        StringBuilder sb = new StringBuilder(json);
        // If truncated mid-string, close the string first
        if (inString) sb.append('"');
        // Remove trailing comma before closing
        String trimmed = sb.toString().stripTrailing();
        if (trimmed.endsWith(",")) sb = new StringBuilder(trimmed.substring(0, trimmed.length() - 1));
        // Close all unclosed brackets/braces
        while (!stack.isEmpty()) sb.append(stack.pop());

        return sb.toString();
    }

    public static boolean doValidateJsonSchema(String json, String SCHEMA_STR) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JSONObject rawSchema = new JSONObject(new JSONTokener(SCHEMA_STR));
        Schema schema = SchemaLoader.load(rawSchema);
        try {
            schema.validate(new JSONObject(jsonNode.toString()));
            return true;
        } catch (Exception ex) {
            log.error("Found an exception : {}", ex.getMessage());
            return false;
        }
    }

    static void resolveOffsets(List<EvaluationErrorList> errorList, String rawHtml) {
        String plainText = stripHtml(rawHtml);
        errorList.forEach(error -> {
            error.setUuid(KeyGeneratorUtil.uuid());
            error.setRefId(KeyGeneratorUtil.refId());
            String phrase = error.getIncorrectText();
            if (StringUtils.isEmpty(phrase)) {
                error.setStart(-1);
                error.setEnd(-1);
                return;
            }
            int start = plainText.indexOf(phrase);
            if (start == -1) {
                // fallback: case-insensitive search
                start = plainText.toLowerCase().indexOf(phrase.toLowerCase());
            }
            error.setStart(start);
            error.setEnd(start == -1 ? -1 : start + phrase.length());
        });
    }

    private static String stripHtml(String html) {
        if (StringUtils.isBlank(html)) return "";
        return html
                .replaceAll("(?i)</p>", "\n")
                .replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("<[^>]+>", "")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .trim();
    }

    static class OllamaJsonRepairer {

        public static String repairJson(String json) {

            // Remove wrapping quotes if entire response is quoted JSON
            if (json.startsWith("\"{") && json.endsWith("}\"")) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    json = mapper.readValue(json, String.class);
                } catch (Exception ignored) {
                }
            }

            // Fix array elements that start with \" instead of "
            json = json.replaceAll(
                    "(\\[|,)\\s*\\\\\"",
                    "$1 \""
            );

            return json;
        }
    }
}

