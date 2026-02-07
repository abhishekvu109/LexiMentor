package com.abhi.leximentor.fitmate.analytics;

import com.abhi.leximentor.fitmate.entities.Drill;

public final class AnalyticsMath {
    private AnalyticsMath() {
    }

    public static double volume(Drill drill) {
        if (drill == null) {
            return 0.0;
        }
        int reps = drill.getRepetition() > 0 ? drill.getRepetition() : 0;
        double measurement = drill.getMeasurement();
        if (measurement > 0 && reps > 0) {
            return measurement * reps;
        }
        if (measurement > 0) {
            return measurement;
        }
        return Math.max(reps, 0);
    }

    public static double safeAverage(double sum, long count) {
        return count > 0 ? sum / count : 0.0;
    }
}
