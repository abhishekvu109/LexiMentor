package com.abhi.leximentor.inventory.constants;

public enum DrillTypes {

    LEARN_MEANING, LEARNING_SPELLINGS, LEARN_POS, LEARN_PRONUNCIATION, LEARN_MISSING_CHARACTERS;

    public static DrillTypes getType(String drillTypes) {
        return switch (drillTypes.toUpperCase()) {
            case "MEANING" -> LEARN_MEANING;
            case "SPELLINGS" -> LEARNING_SPELLINGS;
            case "POS" -> LEARN_POS;
            case "PRONUNCIATION" -> LEARN_PRONUNCIATION;
            default -> LEARN_MISSING_CHARACTERS;
        };
    }
}