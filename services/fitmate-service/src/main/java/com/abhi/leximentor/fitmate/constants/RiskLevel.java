package com.abhi.leximentor.fitmate.constants;

import org.apache.commons.lang3.StringUtils;

public enum RiskLevel {
    LOW(1, "LOW"),
    MEDIUM(5, "MEDIUM"),
    HIGH(10, "HIGH"),
    UNKNOWN(-1, "UNKNOWN");

    private final int score;
    private final String category;

    RiskLevel(int score, String category) {
        this.score = score;
        this.category = category;
    }

    public int getScore() {
        return score;
    }

    public String getCategory() {
        return category;
    }

    // Optional: Static method to get Difficulty by score
    public static RiskLevel parse(Integer score) {
        if (score == null) {
            return UNKNOWN;
        }
        return switch (score) {
            case 1 -> LOW;
            case 5 -> MEDIUM;
            case 10 -> HIGH;
            default -> UNKNOWN;
        };
    }

    public static RiskLevel parse(String level) {
        if (StringUtils.isEmpty(level)) {
            return UNKNOWN;
        }

        if (StringUtils.equalsIgnoreCase("low", level)) {
            return LOW;
        } else if (StringUtils.equalsIgnoreCase("medium", level)) {
            return MEDIUM;
        } else if (StringUtils.equalsIgnoreCase("high", level)) {
            return HIGH;
        } else {
            return UNKNOWN;
        }
    }
}
