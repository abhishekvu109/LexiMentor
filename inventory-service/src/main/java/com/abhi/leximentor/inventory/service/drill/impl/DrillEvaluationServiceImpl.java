package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
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
import com.abhi.leximentor.inventory.entities.inv.PartsOfSpeech;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.inventory.service.drill.DrillEvaluationService;
import com.abhi.leximentor.inventory.util.LLMPromptBuilder;
import com.abhi.leximentor.inventory.util.RestAdvancedUtil;
import com.abhi.leximentor.inventory.util.RestClient;
import com.abhi.leximentor.inventory.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DrillEvaluationServiceImpl implements DrillEvaluationService {

    private final DrillEvaluationRepository drillEvaluationRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final RestAdvancedUtil restUtil;
    private final RestClient restClient;
    private final RestUtil restUtil2;
    private final DrillSetRepository drillSetRepository;
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;
    private final DrillChallengeRepository drillChallengeRepository;
    private String url;

    @Override
    @Transactional
    public DrillEvaluationDTO add(DrillEvaluationDTO dto) {
        Evaluator evaluator = evaluatorRepository.findByName(dto.getEvaluator());
        DrillChallengeScores drillChallengeScores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getDrillChallengeScoresDTO().getRefId()));
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
    public List<DrillEvaluationDTO> evaluateMeaning(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, String evaluator) {
        log.info("Initiated the meaning evaluation. The evaluator is: {}", evaluator);
        List<DrillEvaluationDTO> drillEvaluationDTOS = new LinkedList<>();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        int totalWords = drillChallengeScoresDTOS.size();
        log.info("Total words to evaluate:{}", totalWords);
        int totalCorrect = 0;
        int totalIncorrect = 0;
        DrillChallenge drillChallenge = null;
        for (DrillChallengeScoresDTO dto : drillChallengeScoresDTOS) {
            DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
            WordMetadata wordMetadata = drillSet.getWordId();
            String prompt = getPrompt(wordMetadata.getWord(), wordMetadata.getMeanings().get(0).getDefinition(), dto.getResponse());
            log.info("Successfully formatted the prompt : {}", prompt);

            try {
                Properties properties = PropertiesLoaderUtils.loadProperties(new FileUrlResource("application.properties"));
                setUrl(properties.getProperty(evaluator));
                log.info("Successfully found the evaluator address: {}", properties.getProperty(evaluator));
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
            LlamaModelDTO request = LlamaModelDTO.builder().text(prompt).explanation("").confidence(0).build();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            ResponseEntity<LlamaModelDTO> responseEntity = null;
            try {
                responseEntity = restClient.post(url, headers, request, LlamaModelDTO.class);
            } catch (Exception ex) {
                log.error("Unable to get response from the evaluator {} for {}", evaluator, request);
                log.error(ex.getMessage());
            }
            LlamaModelDTO llamaModelDTO = responseEntity.getBody();
            log.info("The evaluator service has returned a response : {}", responseEntity);
            log.info("The evaluator service has returned a response : {}", llamaModelDTO);
            DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
            drillChallenge = (drillChallenge == null) ? scores.getChallengeId() : drillChallenge;
            scores.setCorrect(llamaModelDTO.isCorrect());
            totalCorrect += llamaModelDTO.isCorrect() ? 1 : 0;
            totalIncorrect += llamaModelDTO.isCorrect() ? 0 : 1;
            drillChallengeScores.add(scores);
            drillEvaluationDTOS.add(DrillEvaluationDTO.builder().drillChallengeScoresDTO(dto).reason(llamaModelDTO.getExplanation()).confidence(llamaModelDTO.getConfidence()).evaluator(evaluator).build());
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
        DrillTypes drillType = DrillTypes.getType(challenge.getDrillType());
        challenge.setEvaluationStatus(Status.DrillChallenge.IN_PROGRESS);
        drillChallengeRepository.save(challenge);
        try {
            return switch (drillType) {
                case IDENTIFY_WORD -> evaluateIdentify(drillChallengeScoresDTOS, challenge);
                case GUESS_WORD -> evaluateGuess(drillChallengeScoresDTOS, challenge);
                case LEARN_POS -> evaluatePOS(drillChallengeScoresDTOS, challenge);
                default -> evaluateMeaning(drillChallengeScoresDTOS, evaluator);
            };
        } catch (Exception ex) {
            challenge.setEvaluationStatus(Status.DrillChallenge.NOT_INITIATED);
            drillChallengeRepository.save(challenge);
            log.error("The evaluation is failed for some internal reasons {}", ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(new ServerException().new InternalError(ex.getMessage()));
        }
    }
}
