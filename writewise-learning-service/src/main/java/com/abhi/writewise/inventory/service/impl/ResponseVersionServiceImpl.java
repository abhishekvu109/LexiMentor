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
import com.abhi.writewise.inventory.util.RestClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResponseVersionServiceImpl implements ResponseVersionService {
    private final ResponseMasterRepository responseMasterRepository;
    private final LLMService llmService;
    private final RepositoryUtil repositoryUtil;
    private final RestClient restClient;

    @Value("${app-util-service}")
    private String APP_UTIL_URL;

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
        return isHighLevel ? LLMPromptBuilder.EvaluationPrompt.getHighLevelEvaluationPrompt(topic.getTopic(), topic.getSubject(), mergedPoints.substring(0, mergedPoints.length() - 1), mergedRecommendations.substring(0, mergedRecommendations.length() - 1), responseVersion.getResponse()) : LLMPromptBuilder.EvaluationPrompt.getLowLevelEvaluationPrompt(topic.getTopic(), topic.getSubject(), mergedPoints.substring(0, mergedPoints.length() - 1), responseVersion.getResponse());
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
        PromptRequest promptRequest = PromptRequest.builder().format(isHighLevel ? StringConstants.LLM_EVALUATION_HIGH_LEVEL : StringConstants.LLM_EVALUATION_LOW_LEVEL).model(StringUtils.isNotEmpty(this.model) ? this.model : ModelConstants.GEMMA3).prompt(StringUtils.isNotEmpty(this.prompt) ? prompt : "").build();
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
            /*--------------------------------------Persistence in the valuation list starts here.----------------------------------------*/
            // Search if there exist an evaluation in the list by evaluator name.
            Evaluation isExistingEvaluation = isExistingEvaluation(responseVersion);
            if (isExistingEvaluation != null && isExistingEvaluation.getEvaluationResult() != null) {
                log.error("The model evaluator {} has already evaluated the writeup.", this.model);
                throw new ServerException().new InternalError(String.format("The model evaluator %s has already evaluated the writeup.", this.model));
            } else if (isExistingEvaluation != null) {
                // The existing evaluation didn't have the high-level evaluation results.
                isExistingEvaluation.setEvaluationResult(evaluationResult);
            } else {
                // The evaluator model has never evaluated it before.
                log.info("The model evaluator {} has never evaluated it before.", this.model);
                evaluation.setEvaluator(this.model);
                List<Evaluation> evaluations = responseVersion.getEvaluations();
                if (CollectionUtils.isEmpty(evaluations)) {
                    evaluations = new ArrayList<>();
                }
                evaluations.add(evaluation);
                responseVersion.setEvaluations(evaluations);
            }
            /*--------------------------------------Persistence in the evaluation list ends here. ----------------------------------------*/
            responseVersion.setEvaluation(evaluation);
            responseVersion.setLlmEvaluationText("");
            responseVersion.setLlmEvaluationStatus(Status.EvaluationStatus.NOT_STARTED);
        } else {
            String lowLevelResult = responseVersion.getLowLevelEvaluationText();
            Evaluation appUtilStartEndIndexRefactor = ResponseVersionUtil.doCallAppUtil(APP_UTIL_URL, restClient, new LowLevelAppUtilDTO(responseVersion.getResponse(), lowLevelResult));
            if (appUtilStartEndIndexRefactor == null || CollectionUtils.isEmpty(appUtilStartEndIndexRefactor.getErrorList())) {
                throw new ServerException().new InternalError("App Util response is null");
            }
            if (StringUtils.isEmpty(lowLevelResult)) {
                throw new ServerException().new InternalError("Please generate the low-level evaluation results.");
            }
            List<EvaluationErrorList> errorList = ResponseVersionUtil.fromJsonStrToEvaluation(lowLevelResult).getErrorList();
            errorList.forEach(error -> {
                EvaluationErrorList evaluationErrorListFrmAppUtil = appUtilStartEndIndexRefactor.getErrorList().stream().filter(e -> StringUtils.equals(e.getId(), error.getId())).findAny().orElse(null);
                error.setUuid(KeyGeneratorUtil.uuid());
                error.setRefId(KeyGeneratorUtil.refId());
                error.setStart(evaluationErrorListFrmAppUtil != null ? evaluationErrorListFrmAppUtil.getStart() : -1);
                error.setEnd(evaluationErrorListFrmAppUtil != null ? evaluationErrorListFrmAppUtil.getEnd() : -1);
            });
            evaluation.setErrorList(errorList);
            /*--------------------------------------Persistence in the valuation list starts here.----------------------------------------*/
            // Search if there exist an evaluation in the list by evaluator name.
            Evaluation isExistingEvaluation = isExistingEvaluation(responseVersion);
//            if (isExistingEvaluation != null && CollectionUtils.isNotEmpty(isExistingEvaluation.getErrorList())) {
//                log.error("The model evaluator {} has already evaluated on low-level the writeup.", this.model);
//                throw new ServerException().new InternalError(String.format("The model evaluator %s has already evaluated the writeup.", this.model));
//            } else

            if (isExistingEvaluation != null) {
                // The existing evaluation didn't have the high-level evaluation results.
                isExistingEvaluation.setErrorList(errorList);
            } else {
                // The evaluator model has never evaluated it before.
                log.info("The model evaluator {} has never evaluated it before.", this.model);
                evaluation.setEvaluator(this.model);
                List<Evaluation> evaluations = responseVersion.getEvaluations();
                if (CollectionUtils.isEmpty(evaluations)) {
                    evaluations = Collections.emptyList();
                }
                evaluations.add(evaluation);
                responseVersion.setEvaluations(evaluations);

            }
            /*--------------------------------------Persistence in the evaluation list ends here. ----------------------------------------*/
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
}

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ResponseVersionUtil {
    public static EvaluationResult fromJsonStrToEvaluationResult(String object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            EvaluationResult evaluationResult = mapper.readValue(object, EvaluationResult.class);
            evaluationResult.setRefId(KeyGeneratorUtil.refId());
            evaluationResult.setUuid(KeyGeneratorUtil.uuid());
            return evaluationResult;
        } catch (Exception e) {
            throw new ServerException().new InternalError("Unable to parse string to EvaluationResult object.");
        }
    }

    public static Evaluation fromJsonStrToEvaluation(String object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(object, Evaluation.class);
        } catch (Exception e) {
            throw new ServerException().new InternalError("Unable to parse string to Evaluation object.");
        }
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

    public static Evaluation doCallAppUtil(String URL, RestClient restClient, LowLevelAppUtilDTO request) {
        String PATH = "app-util/v1/low-level-evaluation/fix-indexes";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Evaluation> responseEntity;
        Evaluation responseOutput;
        try {
            responseEntity = restClient.post(URL + PATH, headers, request, Evaluation.class);
            responseOutput = responseEntity.getBody();
            log.info("The llm service service has returned a response : {}", responseEntity);
            return responseOutput;
        } catch (Exception ex) {
            log.error("Unable to get response from the llm service {} for {}", URL, request);
            log.error(ex.getMessage());
            throw new ServerException().new InternalError(ex.getMessage());
        }
    }
}

record LowLevelAppUtilDTO(
        String text,
        String errorList
) {
}
