package com.abhi.leximentor.fitmate.analytics;

import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsPipeline {
    private final List<AnalyticsCalculator> calculators;

    public AnalyticsDTO run(AnalyticsContext context) {
        AnalyticsDTO.AnalyticsDTOBuilder builder = AnalyticsDTO.builder();
        calculators.forEach(calculator -> {
            calculator.apply(context, builder);
        });
        return builder.build();
    }

    public AnalyticsDTO run(AnalyticsContext context, List<Class<? extends AnalyticsCalculator>> calculatorTypes) {
        if (calculatorTypes == null || calculatorTypes.isEmpty()) {
            return run(context);
        }
        Set<Class<? extends AnalyticsCalculator>> typeSet = Collections.unmodifiableSet(
                calculatorTypes.stream().collect(Collectors.toSet())
        );
        AnalyticsDTO.AnalyticsDTOBuilder builder = AnalyticsDTO.builder();
        calculators.stream()
                .filter(calculator -> typeSet.stream().anyMatch(type -> type.isAssignableFrom(calculator.getClass())))
                .forEach(calculator -> calculator.apply(context, builder));
        return builder.build();
    }
}
