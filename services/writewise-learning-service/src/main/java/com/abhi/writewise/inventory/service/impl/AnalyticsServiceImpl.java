package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.ModelConstants;
import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.analytics.InsightDTO;
import com.abhi.writewise.inventory.dto.analytics.ScoreTrendDTO;
import com.abhi.writewise.inventory.dto.analytics.TopErrorDTO;
import com.abhi.writewise.inventory.dto.analytics.WritingAnalyticsDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.Evaluation;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationErrorList;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationMetric;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationResult;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseVersion;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.model.PromptRequest;
import com.abhi.writewise.inventory.model.PromptResponse;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.service.AnalyticsService;
import com.abhi.writewise.inventory.service.LLMService;
import com.abhi.writewise.inventory.util.LLMPromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ResponseMasterRepository responseMasterRepository;
    private final LLMService llmService;

    @Override
    public WritingAnalyticsDTO getInstantAnalytics() {
        log.info("Computing instant analytics...");

        // Collect all completed evaluations
        List<EvaluationData> evaluations = new ArrayList<>();
        List<ResponseMaster> responseMasters = responseMasterRepository.findAll();

        for (ResponseMaster responseMaster : responseMasters) {
            if (CollectionUtils.isEmpty(responseMaster.getTopicResponseList())) continue;
            for (Response response : responseMaster.getTopicResponseList()) {
                if (CollectionUtils.isEmpty(response.getResponseVersions())) continue;
                for (ResponseVersion version : response.getResponseVersions()) {
                    Evaluation eval = version.getEvaluation();
                    if (eval != null && eval.getEvaluationStatus() == Status.EvaluationStatus.COMPLETED) {
                        evaluations.add(new EvaluationData(version, response, eval));
                    }
                }
            }
        }

        if (evaluations.isEmpty()) {
            log.warn("No completed evaluations found");
            return WritingAnalyticsDTO.builder()
                    .totalEssays(0)
                    .overallAverageScore(0)
                    .improvementRate(0)
                    .scoreTrend(new ArrayList<>())
                    .categoryAverages(new HashMap<>())
                    .errorDistribution(new HashMap<>())
                    .topErrors(new ArrayList<>())
                    .strengths(new ArrayList<>())
                    .weaknesses(new ArrayList<>())
                    .build();
        }

        // Sort by date for trend and improvement rate
        evaluations.sort(Comparator.comparing(e -> e.version.getCreateDate()));

        // Build score trend
        List<ScoreTrendDTO> scoreTrend = evaluations.stream()
                .map(e -> ScoreTrendDTO.builder()
                        .versionNumber(e.version.getVersionNumber())
                        .score(e.eval.getScore())
                        .topicName(e.response.getTopic() != null ? e.response.getTopic().getTopic() : "Unknown")
                        .date(e.version.getCreateDate())
                        .build())
                .collect(Collectors.toList());

        // Compute averages
        double overallAverageScore = evaluations.stream()
                .mapToDouble(e -> e.eval.getScore())
                .average()
                .orElse(0);

        // Compute improvement rate
        double improvementRate = 0;
        if (evaluations.size() >= 2) {
            double firstScore = evaluations.get(0).eval.getScore();
            double lastScore = evaluations.get(evaluations.size() - 1).eval.getScore();
            if (firstScore > 0) {
                improvementRate = ((lastScore - firstScore) / firstScore) * 100;
            }
        }

        // Category averages and error distribution
        Map<String, Double> categoryAverages = new HashMap<>();
        Map<String, Long> errorCounts = new HashMap<>();

        for (String category : Arrays.asList("Spelling", "Grammar", "Punctuation", "Vocabulary", "Style & Tone", "Creativity & Thinking")) {
            categoryAverages.put(category, 0.0);
        }

        for (EvaluationData ed : evaluations) {
            // Aggregate category scores
            if (ed.eval.getEvaluationResult() != null) {
                addCategoryScore(categoryAverages, "Spelling", ed.eval.getEvaluationResult().getSpelling());
                addCategoryScore(categoryAverages, "Grammar", ed.eval.getEvaluationResult().getGrammar());
                addCategoryScore(categoryAverages, "Punctuation", ed.eval.getEvaluationResult().getPunctuation());
                addCategoryScore(categoryAverages, "Vocabulary", ed.eval.getEvaluationResult().getVocabulary());
                addCategoryScore(categoryAverages, "Style & Tone", ed.eval.getEvaluationResult().getStyleAndTone());
                addCategoryScore(categoryAverages, "Creativity & Thinking", ed.eval.getEvaluationResult().getCreativityAndThinking());
            }

            // Aggregate error distribution
            if (CollectionUtils.isNotEmpty(ed.eval.getErrorList())) {
                for (EvaluationErrorList error : ed.eval.getErrorList()) {
                    String key = error.getType();
                    errorCounts.put(key, errorCounts.getOrDefault(key, 0L) + 1);
                }
            }
        }

        // Normalize category averages
        for (Map.Entry<String, Double> entry : categoryAverages.entrySet()) {
            if (evaluations.size() > 0) {
                categoryAverages.put(entry.getKey(), entry.getValue() / evaluations.size());
            }
        }

        // Compute top errors and error distribution percentages
        Map<String, Long> errorDistribution = new HashMap<>();
        long totalErrors = 0;
        for (EvaluationData ed : evaluations) {
            if (CollectionUtils.isNotEmpty(ed.eval.getErrorList())) {
                totalErrors += ed.eval.getErrorList().size();
            }
        }

        Map<String, Long> topErrorsMap = new HashMap<>();
        for (EvaluationData ed : evaluations) {
            if (CollectionUtils.isNotEmpty(ed.eval.getErrorList())) {
                for (EvaluationErrorList error : ed.eval.getErrorList()) {
                    String key = error.getType() + "|" + error.getSubType();
                    topErrorsMap.put(key, topErrorsMap.getOrDefault(key, 0L) + 1);
                }
            }
        }

        // Error distribution by type percentage
        for (Map.Entry<String, Long> entry : errorCounts.entrySet()) {
            errorDistribution.put(entry.getKey(), entry.getValue());
        }

        // Top 10 errors
        List<TopErrorDTO> topErrors = topErrorsMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(e -> {
                    String[] parts = e.getKey().split("\\|");
                    if (parts.length == 0) {
                        log.warn("Invalid error key format: {}", e.getKey());
                        return TopErrorDTO.builder()
                                .type("")
                                .subType("")
                                .count(e.getValue())
                                .build();
                    }
                    return TopErrorDTO.builder()
                            .type(parts[0])
                            .subType(parts.length > 1 ? parts[1] : "")
                            .count(e.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        // Strengths and weaknesses
        List<String> strengths = categoryAverages.entrySet().stream()
                .filter(e -> e.getValue() >= 85)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> weaknesses = categoryAverages.entrySet().stream()
                .filter(e -> e.getValue() < 70)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.info("Analytics computed: {} essays, avg score: {}, improvement: {}%", evaluations.size(), overallAverageScore, improvementRate);

        return WritingAnalyticsDTO.builder()
                .totalEssays(evaluations.size())
                .overallAverageScore(overallAverageScore)
                .improvementRate(improvementRate)
                .scoreTrend(scoreTrend)
                .categoryAverages(categoryAverages)
                .errorDistribution(errorDistribution)
                .topErrors(topErrors)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .build();
    }

    @Override
    public InsightDTO generateLlmInsights() {
        log.info("Generating LLM insights...");

        WritingAnalyticsDTO analytics = getInstantAnalytics();

        // Build compact summary for LLM
        String summary = buildAnalyticsSummary(analytics);
        log.debug("Analytics summary for LLM: {}", summary);

        // Call LLM
        PromptRequest promptRequest = PromptRequest.builder()
                .prompt(LLMPromptBuilder.AnalyticsPrompt.getInsightPrompt(summary))
                .model(ModelConstants.CLOUD_LLM)
                .options(ApplicationConstants.DEFAULT_OLLAMA_OPTIONS)
                .build();

        int retryCount = 3;
        PromptResponse promptResponse = null;
        while (retryCount > 0) {
            try {
                promptResponse = llmService.execute(promptRequest);
                break;
            } catch (Exception ex) {
                log.error("LLM insight generation failed (attempt {}): {}", 4 - retryCount, ex.getMessage());
                retryCount--;
            }
        }

        if (promptResponse == null || StringUtils.isEmpty(promptResponse.getResponse())) {
            throw new ServerException().new InternalError("Failed to generate insights from LLM");
        }

        log.info("LLM insights generated successfully");

        return InsightDTO.builder()
                .insight(promptResponse.getResponse())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private void addCategoryScore(Map<String, Double> categoryAverages, String categoryName, EvaluationMetric metric) {
        if (metric != null) {
            categoryAverages.put(categoryName, categoryAverages.get(categoryName) + metric.getScore());
        }
    }

    private String buildAnalyticsSummary(WritingAnalyticsDTO analytics) {
        StringBuilder summary = new StringBuilder();
        summary.append("Total essays: ").append(analytics.getTotalEssays());
        summary.append(", Overall avg score: ").append(String.format("%.1f", analytics.getOverallAverageScore()));
        summary.append(", Improvement: ").append(String.format("+%.1f", analytics.getImprovementRate())).append("%\n");

        summary.append("Category scores: ");
        analytics.getCategoryAverages().forEach((cat, score) ->
                summary.append(cat).append("=").append(String.format("%.0f", score)).append(", ")
        );
        summary.append("\n");

        summary.append("Top errors: ");
        analytics.getTopErrors().stream().limit(5).forEach(err ->
                summary.append(err.getSubType()).append(" (").append(err.getCount()).append("x), ")
        );
        summary.append("\n");

        summary.append("Error distribution: ");
        long totalErrors = analytics.getErrorDistribution().values().stream().mapToLong(Long::longValue).sum();
        if (totalErrors > 0) {
            analytics.getErrorDistribution().forEach((type, count) ->
                    summary.append(type).append("=").append(String.format("%.0f", (count * 100.0 / totalErrors))).append("%, ")
            );
        }
        summary.append("\n");

        summary.append("Strengths: ").append(String.join(", ", analytics.getStrengths())).append(". ");
        summary.append("Weaknesses: ").append(String.join(", ", analytics.getWeaknesses())).append(".");

        return summary.toString();
    }

    private static class EvaluationData {
        ResponseVersion version;
        Response response;
        Evaluation eval;

        EvaluationData(ResponseVersion version, Response response, Evaluation eval) {
            this.version = version;
            this.response = response;
            this.eval = eval;
        }
    }
}
