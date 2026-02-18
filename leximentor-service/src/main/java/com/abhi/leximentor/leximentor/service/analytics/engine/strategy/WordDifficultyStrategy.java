package com.abhi.leximentor.leximentor.service.analytics.engine.strategy;

import com.abhi.leximentor.leximentor.dto.analytics.WordDifficultyDTO;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsRequest;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsStrategy;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordDifficultyStrategy implements AnalyticsStrategy<List<WordDifficultyDTO>> {
    private static final int DEFAULT_TOP_N = 20;

    private final DrillChallengeScoreRepository drillChallengeScoreRepository;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.WORD_DIFFICULTY;
    }

    @Override
    public List<WordDifficultyDTO> execute(AnalyticsRequest request) {
        int topN = request.getTopN() == null ? DEFAULT_TOP_N : request.getTopN();

        List<Object[]> rows = drillChallengeScoreRepository.findWordDifficultyHeatmap(topN);
        List<WordDifficultyDTO> results = new LinkedList<>();
        for (Object[] row : rows) {
            long wordRefId = row[0] == null ? 0L : ((Number) row[0]).longValue();
            String word = row[1] == null ? "" : String.valueOf(row[1]);
            long wrongCount = row[2] == null ? 0L : ((Number) row[2]).longValue();
            results.add(WordDifficultyDTO.builder()
                    .wordRefId(wordRefId)
                    .word(word)
                    .wrongCount(wrongCount)
                    .build());
        }

        return results;
    }
}
