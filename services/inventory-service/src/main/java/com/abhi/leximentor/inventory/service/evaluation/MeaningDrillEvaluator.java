package com.abhi.leximentor.inventory.service.evaluation;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.constants.StringConstant;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.dto.other.LlamaModelDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillEvaluation;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.model.EvaluationResult;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.inventory.service.drill.impl.DrillServiceUtil;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import com.abhi.leximentor.inventory.util.LLMPromptBuilder;
import com.abhi.llm.constants.ModelConstant;
import com.abhi.llm.model.PromptRequest;
import com.abhi.llm.model.PromptResponse;
import com.abhi.llm.service.LLMService;
import com.abhi.llm.service.impl.LLMServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeaningDrillEvaluator implements DrillEvaluator {

    private final DrillSetRepository drillSetRepository;
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;
    private final DrillChallengeRepository drillChallengeRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final DrillEvaluationRepository drillEvaluationRepository;

    private List<DrillChallengeScoresDTO> drillChallengeScoresDTOS;
    private DrillChallenge drillChallenge;
    private String LLM_URL;
    private String LLM_MODEL;

    private String EVALUATOR;

    public MeaningDrillEvaluator init(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge, String LLM_URL, String LLM_MODEL, String evaluator) {
        this.drillChallengeScoresDTOS = drillChallengeScoresDTOS;
        this.drillChallenge = drillChallenge;
        this.LLM_URL = LLM_URL;
        this.LLM_MODEL = LLM_MODEL;
        this.EVALUATOR = evaluator;
        return this;
    }


    @Override
    public EvaluationResult evaluate() {
        log.info("Initiated the meaning evaluation.");
        LLMService llmService = new LLMServiceImpl(LLM_URL);
        List<DrillEvaluationDTO> drillEvaluationDTOS = new LinkedList<>();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        int totalWords = drillChallengeScoresDTOS.size();
        log.info("Total words to evaluate:{}", totalWords);
        int totalCorrect = 0;
        int totalIncorrect = 0;
        for (DrillChallengeScoresDTO dto : this.drillChallengeScoresDTOS) {
            if (StringUtils.isNotEmpty(dto.getResponse())) {
                DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
                WordMetadata wordMetadata = drillSet.getWordId();
                String prompt = getPrompt(wordMetadata.getWord(), wordMetadata.getMeanings().get(0).getDefinition(), dto.getResponse());
                log.info("Successfully formatted the prompt : {}", prompt);
                PromptRequest promptRequest = new PromptRequest();
                promptRequest.setModel(LLM_MODEL);
                promptRequest.setPrompt(prompt);
                promptRequest.setOptions(ModelConstant.DEFAULT_OLLAMA_OPTIONS);
                promptRequest.setFormat(StringConstant.MODEL_RESPONSE_FORMAT_MEANING_EVALUATION);
                PromptResponse promptResponse = llmService.execute(promptRequest);
                LlamaModelDTO llamaModelDTO = StringUtils.isNotEmpty(dto.getResponse()) ?
                        fromPromptResponseToDTO(promptResponse) :
                        LlamaModelDTO.builder().correct(false).explanation("Response was empty").confidence(100).build();
                llamaModelDTO = llamaModelDTO == null ? LlamaModelDTO.getDefaultInstance() : llamaModelDTO;
                log.info("The evaluator service has returned a response : {}", llamaModelDTO);
                DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
                drillChallenge = (drillChallenge == null) ? scores.getChallengeId() : drillChallenge;
                scores.setCorrect(llamaModelDTO.isCorrect());
                totalCorrect += llamaModelDTO.isCorrect() ? 1 : 0;
                log.info("Total correct in the challenge: {}", totalCorrect);
                totalIncorrect += llamaModelDTO.isCorrect() ? 0 : 1;
                drillChallengeScores.add(scores);
                drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason(llamaModelDTO.getExplanation()).confidence(llamaModelDTO.getConfidence()).evaluator(EVALUATOR).build());
            } else {
                log.info("The user has not put a response.");
                DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
                drillChallenge = (drillChallenge == null) ? scores.getChallengeId() : drillChallenge;
                scores.setCorrect(false);
                totalCorrect += 0;
                totalIncorrect += 1;
                drillChallengeScores.add(scores);
                drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason("Response was empty").confidence(100).evaluator(EVALUATOR).build());
            }

        }
        drillChallengeScoreRepository.saveAll(drillChallengeScores);
        log.info("Saved all the drill scores");
        drillChallenge.setDrillScore(totalCorrect);
        drillChallenge.setTotalCorrect(totalCorrect);
        drillChallenge.setTotalWrong(totalIncorrect);
        drillChallenge.setStatus(Status.DrillChallenge.EVALUATED);
        drillChallenge.setDrillScore(DrillServiceUtil.DrillChallengeUtil.score(totalCorrect, totalIncorrect));
        drillChallenge.setPass(DrillServiceUtil.DrillChallengeUtil.isPass(drillChallenge.getDrillScore()));
        drillChallenge = drillChallengeRepository.save(drillChallenge);
        log.info("Saved the results in the challenge entity. {}", drillChallenge);
        return EvaluationResult.builder().isSuccess(true).result(drillEvaluationDTOS.stream().map(this::add).collect(Collectors.toList())).build();
    }

    private String getPrompt(String word, String originalMeaning, String response) {
        return LLMPromptBuilder.EvaluationModule.getPrompt2(word, originalMeaning, response);
    }

    private LlamaModelDTO fromPromptResponseToDTO(PromptResponse response) {
        if (StringUtils.isNotEmpty(response.getResponse())) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(response.getResponse(), LlamaModelDTO.class);
            } catch (Exception e) {
                throw new ServerException().new InternalError("Unable to parse string to EvaluationResult object.");
            }
        }
        throw new ServerException().new InternalError("Prompt response is empty.");
    }

    @Transactional
    public DrillEvaluationDTO add(DrillEvaluationDTO dto) {
        DrillChallengeScores drillChallengeScores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getDrillChallengeScoresDTO().getRefId()));
        Evaluator evaluator = evaluatorRepository.findByNameAndDrillType(dto.getEvaluator(), drillChallengeScores.getChallengeId().getDrillType());
        DrillEvaluation drillEvaluation = this.getDrillEvaluation(dto,drillChallengeScores,evaluator);
        drillEvaluation = drillEvaluationRepository.save(drillEvaluation);
        return DrillServiceUtil.DrillEvaluationUtil.buildDTO(drillEvaluation, DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(drillEvaluation.getDrillChallengeScores()));
    }

    private DrillEvaluation getDrillEvaluation(DrillEvaluationDTO dto, DrillChallengeScores drillChallengeScores, Evaluator evaluator) {
        List<DrillEvaluation> drillEvaluations = drillEvaluationRepository.findByDrillChallengeScoresIn(List.of(drillChallengeScores));
        DrillEvaluation drillEvaluation;
        if (CollectionUtil.isNotEmpty(drillEvaluations)) {
            drillEvaluation = drillEvaluations.get(0);
            drillEvaluation.setEvaluator(evaluator);
            drillEvaluation.setEvaluationTime(dto.getEvaluationTime());
            drillEvaluation.setConfidence(dto.getConfidence());
            drillEvaluation.setDrillChallengeScores(drillChallengeScores);
            drillEvaluation.setReason(dto.getReason());
            return drillEvaluation;
        } else {
            return DrillServiceUtil.DrillEvaluationUtil.buildEntity(dto, evaluator, drillChallengeScores);
        }
    }

}
