package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.dto.other.LlamaModelDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillEvaluation;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.inventory.service.drill.DrillEvaluationService;
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
    private final DrillSetRepository drillSetRepository;
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;
    private final DrillChallengeRepository drillChallengeRepository;
    private String url;

    @Override
    @Transactional
    public DrillEvaluationDTO add(DrillEvaluationDTO dto) {
        Evaluator evaluator = evaluatorRepository.findByName(dto.getEvaluator());
        DrillEvaluation drillEvaluation = DrillServiceUtil.DrillEvaluationUtil.buildEntity(dto, evaluator);
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
//            log.info("Found the drillSet object for the refId:{},{}", dto.getDrillSetRefId(), drillSet);
            WordMetadata wordMetadata = drillSet.getWordId();
//            log.info("Found the word object : {}", wordMetadata);
            String prompt = getPrompt(wordMetadata.getWord(), wordMetadata.getMeanings().get(0).getDefinition(), dto.getResponse());
            log.info("Successfully formatted the prompt : {}", prompt);
            try {
                Properties properties = PropertiesLoaderUtils.loadProperties(new FileUrlResource("application.properties"));
                setUrl(properties.getProperty(evaluator));
                log.info("Successfully found the evaluator address: {}", properties.getProperty(evaluator));
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
            LlamaModelDTO request = LlamaModelDTO.builder().text(prompt).build();
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
//            LlamaModelDTO llamaModelDTO = restUtil.post(url, request, LlamaModelDTO.class);
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
        drillChallenge.setTotalCorrect(totalIncorrect);
        drillChallenge = drillChallengeRepository.save(drillChallenge);
        log.info("Saved the results in the challenge entity");
        return this.addAll(drillEvaluationDTOS);
    }

    private String getPrompt(String word, String originalMeaning, String response) {
        String prompt = ApplicationConstants.Prompt.LLAMA_PROMPT;
        return prompt.replace(ApplicationConstants.Prompt.WORD, word).replace(ApplicationConstants.Prompt.ORIGINAL_MEANING, originalMeaning).replace(ApplicationConstants.Prompt.RESPONSE, response);
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }
}
