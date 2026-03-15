package com.abhi.leximentor.fitmate.dto.rec;

import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import lombok.Builder;
import lombok.Data;

/**
 * A single exercise recommendation produced by the rule engine (Phase 1)
 * or the ML model (Phase 2).
 */
@Data
@Builder
public class ExerciseRecommendationDTO {

    /** Full exercise details. */
    private ExerciseDTO exercise;

    // ── Progressive overload targets ─────────────────────────────────────────

    /** Recommended load (weight in kg/lb or resistance level). */
    private double recommendedWeight;

    /** Recommended number of sets. */
    private int recommendedSets;

    /** Recommended number of reps per set. */
    private int recommendedReps;

    /** Unit of the recommended weight (matches {@code Exercise.unit}). */
    private String weightUnit;

    // ── Scoring / transparency ───────────────────────────────────────────────

    /**
     * Normalised neglect score in [0, 1].
     * Higher = more neglected = should be prioritised.
     */
    private double neglectScore;

    /** How many days since this exercise was last performed by this user. */
    private int daysSinceLastDone;

    /**
     * Human-readable note explaining the load change.
     * Examples: "Increase by 2.5 kg", "Maintain current load", "Deload -15%"
     */
    private String progressionNote;

    /**
     * Which engine produced this recommendation.
     * {@code "RULE_ENGINE"} for Phase 1, {@code "ML_MODEL"} for Phase 2.
     */
    private String source;
}
