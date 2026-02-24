package com.abhi.leximentor.leximentor.service.evaluation;

import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.constants.StringConstant;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import com.abhi.leximentor.leximentor.dto.other.LlamaModelDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.entities.inv.Evaluator;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.model.EvaluationResult;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillSetRepository;
import com.abhi.leximentor.leximentor.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import com.abhi.leximentor.leximentor.util.LLMPromptBuilder;
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
    private final ChallengeScoreRepository challengeScoreRepository;
    private final ChallengeRepository challengeRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final ChallengeEvaluationRepository challengeEvaluationRepository;
    private final DrillDomainMapper drillDomainMapper;

    private List<ChallengeScoresDTO> ChallengeScoresDTOS;
    private Challenge challenge;
    private String LLM_URL;
    private String LLM_MODEL;

    private String EVALUATOR;

    public MeaningDrillEvaluator init(List<ChallengeScoresDTO> ChallengeScoresDTOS, Challenge challenge, String LLM_URL, String LLM_MODEL, String evaluator) {
        this.ChallengeScoresDTOS = ChallengeScoresDTOS;
        this.challenge = challenge;
        this.LLM_URL = LLM_URL;
        this.LLM_MODEL = LLM_MODEL;
        this.EVALUATOR = evaluator;
        return this;
    }


    @Override
    public EvaluationResult evaluate() {
        log.info("Initiated the meaning evaluation.");
        LLMService llmService = new LLMServiceImpl(LLM_URL);
        List<ChallengeEvaluationDTO> ChallengeEvaluationDTOS = new LinkedList<>();
        List<ChallengeScores> challengeScores = new LinkedList<>();
        int totalWords = ChallengeScoresDTOS.size();
        log.info("Total words to evaluate:{}", totalWords);
        int totalCorrect = 0;
        int totalIncorrect = 0;
        for (ChallengeScoresDTO dto : this.ChallengeScoresDTOS) {
            if (StringUtils.isNotEmpty(dto.getResponse())) {
                DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
                WordMetadata wordMetadata = drillSet.getWord();
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
                ChallengeScores scores = challengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
                challenge = (challenge == null) ? scores.getChallenge() : challenge;
                scores.setCorrect(llamaModelDTO.isCorrect());
                totalCorrect += llamaModelDTO.isCorrect() ? 1 : 0;
                log.info("Total correct in the challenge: {}", totalCorrect);
                totalIncorrect += llamaModelDTO.isCorrect() ? 0 : 1;
                challengeScores.add(scores);
                ChallengeEvaluationDTOS.add(ChallengeEvaluationDTO.builder().ChallengeScoresDTO(dto).reason(llamaModelDTO.getExplanation()).confidence(llamaModelDTO.getConfidence()).evaluator(EVALUATOR).build());
            } else {
                log.info("The user has not put a response.");
                ChallengeScores scores = challengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
                challenge = (challenge == null) ? scores.getChallenge() : challenge;
                scores.setCorrect(false);
                totalCorrect += 0;
                totalIncorrect += 1;
                challengeScores.add(scores);
                ChallengeEvaluationDTOS.add(ChallengeEvaluationDTO.builder().ChallengeScoresDTO(dto).reason("Response was empty").confidence(100).evaluator(EVALUATOR).build());
            }

        }
        challengeScoreRepository.saveAll(challengeScores);
        log.info("Saved all the drill scores");
        challenge.setScore(totalCorrect);
        challenge.setTotalCorrect(totalCorrect);
        challenge.setTotalWrong(totalIncorrect);
        challenge.setStatus(Status.DrillChallenge.EVALUATED);
        challenge.setScore(drillDomainMapper.score(totalCorrect, totalIncorrect));
        challenge.setPass(drillDomainMapper.isPass(challenge.getScore()));
        challenge = challengeRepository.save(challenge);
        log.info("Saved the results in the challenge entity. {}", challenge);
        return EvaluationResult.builder().isSuccess(true).result(ChallengeEvaluationDTOS.stream().map(this::add).collect(Collectors.toList())).build();
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
    public ChallengeEvaluationDTO add(ChallengeEvaluationDTO dto) {
        ChallengeScores challengeScores = challengeScoreRepository.findByRefId(Long.parseLong(dto.getChallengeScoresDTO().getRefId()));
        Evaluator evaluator = evaluatorRepository.findByNameAndDrillType(dto.getEvaluator(), challengeScores.getChallenge().getChallengeType());
        ChallengeEvaluation challengeEvaluation = this.getDrillEvaluation(dto, challengeScores,evaluator);
        challengeEvaluation = challengeEvaluationRepository.save(challengeEvaluation);
        return drillDomainMapper.toDto(challengeEvaluation, drillDomainMapper.toDto(challengeEvaluation.getChallengeScores()));
    }

    private ChallengeEvaluation getDrillEvaluation(ChallengeEvaluationDTO dto, ChallengeScores challengeScores, Evaluator evaluator) {
        List<ChallengeEvaluation> challengeEvaluations = challengeEvaluationRepository.findByDrillChallengeScoresIn(List.of(challengeScores));
        ChallengeEvaluation challengeEvaluation;
        if (CollectionUtil.isNotEmpty(challengeEvaluations)) {
            challengeEvaluation = challengeEvaluations.get(0);
            challengeEvaluation.setEvaluator(evaluator);
            challengeEvaluation.setEvaluationTime(dto.getEvaluationTime());
            challengeEvaluation.setConfidence(dto.getConfidence());
            challengeEvaluation.setChallengeScores(challengeScores);
            challengeEvaluation.setReason(dto.getReason());
            return challengeEvaluation;
        } else {
            return drillDomainMapper.toEntity(dto, evaluator, challengeScores);
        }
    }

}
