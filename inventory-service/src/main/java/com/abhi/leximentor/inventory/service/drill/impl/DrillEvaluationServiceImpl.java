package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillReportResponseDTO;
import com.abhi.leximentor.inventory.dto.other.LlamaModelDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillEvaluation;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.inventory.service.drill.DrillEvaluationService;
import com.abhi.leximentor.inventory.service.drill.impl.factory.MeaningEvaluator.LlamaMeaningEvaluator;
import com.abhi.leximentor.inventory.service.drill.impl.factory.MeaningEvaluator.OllamaMeaningEvaluator;
import com.abhi.leximentor.inventory.service.drill.impl.factory.MeaningEvaluatorFactory;
import com.abhi.leximentor.inventory.util.LLMPromptBuilder;
import com.abhi.leximentor.inventory.util.RestAdvancedUtil;
import com.abhi.leximentor.inventory.util.RestClient;
import com.abhi.leximentor.inventory.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DrillEvaluationServiceImpl implements DrillEvaluationService {
    public static final Integer LLM_RETRY_COUNT = 3;
    private final DrillEvaluationRepository drillEvaluationRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final RestAdvancedUtil restUtil;
    private final RestClient restClient;
    private final RestTemplate restTemplate;
    private final RestUtil restUtil2;
    private final DrillSetRepository drillSetRepository;
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;
    private final DrillChallengeRepository drillChallengeRepository;
    private String url;

    @Override
    @Transactional
    public DrillEvaluationDTO add(DrillEvaluationDTO dto) {
        DrillChallengeScores drillChallengeScores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getDrillChallengeScoresDTO().getRefId()));
        Evaluator evaluator = evaluatorRepository.findByNameAndDrillType(dto.getEvaluator(), drillChallengeScores.getChallengeId().getDrillType());
        DrillEvaluation drillEvaluation = DrillServiceUtil.DrillEvaluationUtil.buildEntity(dto, evaluator, drillChallengeScores);
        drillEvaluation = drillEvaluationRepository.save(drillEvaluation);
        return DrillServiceUtil.DrillEvaluationUtil.buildDTO(drillEvaluation, DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(drillEvaluation.getDrillChallengeScores()));
    }

    @Override
    @Transactional
    public List<DrillEvaluationDTO> addAll(List<DrillEvaluationDTO> dtos) {
        return dtos.stream().map(this::add).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<DrillEvaluationDTO> evaluateMeaning(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, String evaluator) throws Exception {
        log.info("Initiated the meaning evaluation. The evaluator is: {}", evaluator);
        List<DrillEvaluationDTO> drillEvaluationDTOS = new LinkedList<>();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        int totalWords = drillChallengeScoresDTOS.size();
        log.info("Total words to evaluate:{}", totalWords);
        int totalCorrect = 0;
        int totalIncorrect = 0;
        DrillChallenge drillChallenge = null;
        for (DrillChallengeScoresDTO dto : drillChallengeScoresDTOS) {
            if (StringUtils.isNotEmpty(dto.getResponse())) {
                DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
                WordMetadata wordMetadata = drillSet.getWordId();
                String prompt = getPrompt(wordMetadata.getWord(), wordMetadata.getMeanings().get(0).getDefinition(), dto.getResponse());
                log.info("Successfully formatted the prompt : {}", prompt);
                loadModelServiceName(evaluator);
                LlamaModelDTO llamaModelDTO = StringUtils.isNotEmpty(dto.getResponse()) ? getLlmResponse(prompt, evaluator) : LlamaModelDTO.builder().isCorrect(false).explanation("Response was empty").confidence(100).build();
                llamaModelDTO = llamaModelDTO == null ? LlamaModelDTO.getDefaultInstance() : llamaModelDTO;
                log.info("The evaluator service has returned a response : {}", llamaModelDTO);
                DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
                drillChallenge = (drillChallenge == null) ? scores.getChallengeId() : drillChallenge;
                scores.setCorrect(llamaModelDTO.isCorrect());
                totalCorrect += llamaModelDTO.isCorrect() ? 1 : 0;
                totalIncorrect += llamaModelDTO.isCorrect() ? 0 : 1;
                drillChallengeScores.add(scores);
                drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason(llamaModelDTO.getExplanation()).confidence(llamaModelDTO.getConfidence()).evaluator(evaluator).build());
            } else {
                log.info("The user has not put a response.");
                DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
                drillChallenge = (drillChallenge == null) ? scores.getChallengeId() : drillChallenge;
                scores.setCorrect(false);
                totalCorrect += 0;
                totalIncorrect += 1;
                drillChallengeScores.add(scores);
                drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason("Response was empty").confidence(100).evaluator(evaluator).build());
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
        return this.addAll(drillEvaluationDTOS);
    }

    private void loadModelServiceName(String evaluator) {
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileUrlResource("application.properties"));
            log.info("Successfully found the evaluator address: {}", properties.getProperty(evaluator));
            setUrl(properties.getProperty(evaluator));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private LlamaModelDTO getLlmResponse(String prompt, String evaluator) {
        MeaningEvaluatorFactory meaningEvaluatorFactory = null;
        if (evaluator.equalsIgnoreCase("llama-llm-based-evaluator"))
            meaningEvaluatorFactory = new LlamaMeaningEvaluator(restClient);
        else meaningEvaluatorFactory = new OllamaMeaningEvaluator(restClient);
        return meaningEvaluatorFactory.response(prompt, LLM_RETRY_COUNT);
    }

    private String getPrompt(String word, String originalMeaning, String response) {
        return LLMPromptBuilder.getPrompt(word, originalMeaning, response);
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public DrillReportResponseDTO getEvaluationReport(long challengeRefId) {
        DrillChallenge challenge = drillChallengeRepository.findByRefId(challengeRefId);
        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = challenge.getDrillChallengeScoresList().stream().map(DrillServiceUtil.DrillChallengeScoreUtil::buildDTO).toList();
        List<DrillEvaluationDTO> drillEvaluationDTOS = drillEvaluationRepository.findByDrillChallengeScoresIn(challenge.getDrillChallengeScoresList()).stream().map(evaluation -> DrillServiceUtil.DrillEvaluationUtil.buildDTO(evaluation, DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(evaluation.getDrillChallengeScores()))).toList();
        return DrillReportResponseDTO.builder().challengeRefId(String.valueOf(challenge.getRefId())).evaluator(drillEvaluationDTOS.get(0).getEvaluator()).drillType(challenge.getDrillType()).drillEvaluationDTOS(drillEvaluationDTOS).totalCorrect(challenge.getTotalCorrect()).totalIncorrect(challenge.getTotalWrong()).score(challenge.getDrillScore()).isPassed(challenge.isPass()).build();
    }


    @Transactional
    public List<DrillEvaluationDTO> evaluatePOS(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge) {
        log.info("Initiated the POS evaluation.");
        List<DrillEvaluationDTO> drillEvaluationDTOS = new LinkedList<>();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        int totalWords = drillChallengeScoresDTOS.size();
        log.info("Total words to evaluate:{}", totalWords);
        int totalCorrect = 0;
        int totalIncorrect = 0;
        Evaluator evaluator = evaluatorRepository.findByDrillType(DrillTypes.LEARN_POS.name()).get(0);
        for (DrillChallengeScoresDTO dto : drillChallengeScoresDTOS) {
            DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
            WordMetadata wordMetadata = drillSet.getWordId();
            DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
            boolean isCorrect = wordMetadata.getPartsOfSpeeches().stream().anyMatch(partsOfSpeech -> partsOfSpeech.getPos().equalsIgnoreCase(scores.getResponse()));
            scores.setCorrect(isCorrect);
            totalCorrect += isCorrect ? 1 : 0;
            totalIncorrect += isCorrect ? 0 : 1;
            drillChallengeScores.add(scores);
            drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason(isCorrect ? "The match is found." : "The match is not found.").confidence(100).evaluator(evaluator.getName()).build());
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
        log.info("Saved the results in the challenge entity");
        return this.addAll(drillEvaluationDTOS);
    }

    @Transactional
    public List<DrillEvaluationDTO> evaluateGuess(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge) {
        log.info("Initiated the Guess evaluation.");
        List<DrillEvaluationDTO> drillEvaluationDTOS = new LinkedList<>();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        int totalWords = drillChallengeScoresDTOS.size();
        log.info("Total words to evaluate:{}", totalWords);
        int totalCorrect = 0;
        int totalIncorrect = 0;
        Evaluator evaluator = evaluatorRepository.findByDrillType(DrillTypes.GUESS_WORD.name()).get(0);
        for (DrillChallengeScoresDTO dto : drillChallengeScoresDTOS) {
            DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
            WordMetadata wordMetadata = drillSet.getWordId();
            DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
            boolean isCorrect = wordMetadata.getWord().equalsIgnoreCase(dto.getResponse());
            scores.setCorrect(isCorrect);
            totalCorrect += isCorrect ? 1 : 0;
            totalIncorrect += isCorrect ? 0 : 1;
            drillChallengeScores.add(scores);
            drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason(isCorrect ? "The match is found." : "The match is not found.").confidence(100).evaluator(evaluator.getName()).build());
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
        log.info("Saved the results in the challenge entity");
        return this.addAll(drillEvaluationDTOS);
    }

    @Transactional
    public List<DrillEvaluationDTO> evaluateIdentify(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge) {
        log.info("Initiated the Identify evaluation.");
        List<DrillEvaluationDTO> drillEvaluationDTOS = new LinkedList<>();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        int totalWords = drillChallengeScoresDTOS.size();
        log.info("Total words to evaluate:{}", totalWords);
        int totalCorrect = 0;
        int totalIncorrect = 0;
        Evaluator evaluator = evaluatorRepository.findByDrillType(DrillTypes.IDENTIFY_WORD.name()).get(0);
        for (DrillChallengeScoresDTO dto : drillChallengeScoresDTOS) {
            DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
            WordMetadata wordMetadata = drillSet.getWordId();
            DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
            boolean isCorrect = wordMetadata.getWord().equalsIgnoreCase(dto.getResponse());
            scores.setCorrect(isCorrect);
            totalCorrect += isCorrect ? 1 : 0;
            totalIncorrect += isCorrect ? 0 : 1;
            drillChallengeScores.add(scores);
            drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason(isCorrect ? "The match is found." : "The match is not found.").confidence(100).evaluator(evaluator.getName()).build());
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
        log.info("Saved the results in the challenge entity");
        return this.addAll(drillEvaluationDTOS);
    }

    @Override
    @Transactional
    public List<DrillEvaluationDTO> evaluate(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, String evaluator, long challengeRefId) {
        DrillChallenge challenge = drillChallengeRepository.findByRefId(challengeRefId);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            challenge.setEvaluationStatus(Status.DrillChallenge.IN_PROGRESS);
            drillChallengeRepository.save(challenge);
        });
        try {
            List<DrillEvaluationDTO> drillEvaluationDTOS = null;
            String drillType = challenge.getDrillType();
            if (drillType.equals(DrillTypes.IDENTIFY_WORD.name()))
                drillEvaluationDTOS = evaluateIdentify(drillChallengeScoresDTOS, challenge);
            else if (drillType.equals(DrillTypes.GUESS_WORD.name()))
                drillEvaluationDTOS = evaluateGuess(drillChallengeScoresDTOS, challenge);
            else if (drillType.equals(DrillTypes.LEARN_POS.name()))
                drillEvaluationDTOS = evaluatePOS(drillChallengeScoresDTOS, challenge);
            else drillEvaluationDTOS = evaluateMeaning(drillChallengeScoresDTOS, evaluator);
            challenge.setEvaluationStatus(Status.DrillChallenge.COMPLETED);
            drillChallengeRepository.save(challenge);
            return drillEvaluationDTOS;
        } catch (Exception ex) {
            challenge.setEvaluationStatus(Status.DrillChallenge.NOT_INITIATED);
            drillChallengeRepository.save(challenge);
            log.error("The evaluation is failed for some internal reasons {}", ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(new ServerException().new InternalError(ex.getMessage()));
        }
    }
}
