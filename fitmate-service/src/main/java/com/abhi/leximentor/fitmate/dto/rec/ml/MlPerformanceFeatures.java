package com.abhi.leximentor.fitmate.dto.rec.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Mirrors Python schema {@code PerformanceFeatures} in fitmate-ml.
 * One element in the list sent to {@code POST /predict/performance}.
 */
@Data
@Builder
public class MlPerformanceFeatures {

    @JsonProperty("exercise_ref_id")
    private long exerciseRefId;

    private String username;

    @JsonProperty("last_weight")
    private double lastWeight;

    @JsonProperty("last_reps")
    private int lastReps;

    /** Defaulted to 3 — Drill entity has no explicit sets field. */
    @JsonProperty("last_sets")
    private int lastSets;

    @JsonProperty("personal_record")
    private double personalRecord;

    @JsonProperty("days_since_last_session")
    private int daysSinceLastSession;

    /** Linear slope of weight over the last 5 sessions. */
    @JsonProperty("trend_slope")
    private double trendSlope;

    @JsonProperty("sessions_this_week")
    private int sessionsThisWeek;

    /** e.g. "STRENGTH", "HYPERTROPHY", "ENDURANCE". */
    @JsonProperty("training_type")
    private String trainingType;
}
