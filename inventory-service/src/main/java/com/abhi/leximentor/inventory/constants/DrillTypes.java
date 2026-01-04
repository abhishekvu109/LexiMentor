package com.abhi.leximentor.inventory.constants;

public enum DrillTypes {

    LEARN_MEANING, LEARNING_SPELLINGS, LEARN_POS, LEARN_PRONUNCIATION, LEARN_MISSING_CHARACTERS,
    IDENTIFY_WORD, GUESS_WORD, CONTEXT_MASTER, SPEED_TYPER, FLASHCARD_BLITZ, WORD_SCRAMBLE,MATCH_WORD,
    SENTENCE_USAGE;

    public static DrillTypes getType(String drillTypes) {
        return switch (drillTypes.toUpperCase()) {
            case "MEANING" -> LEARN_MEANING;
            case "SPELLINGS" -> LEARNING_SPELLINGS;
            case "POS" -> LEARN_POS;
            case "PRONUNCIATION" -> LEARN_PRONUNCIATION;
            case "IDENTIFY" -> IDENTIFY_WORD;
            case "GUESS" -> GUESS_WORD;
            case "CM" -> CONTEXT_MASTER;
            case "FB" -> FLASHCARD_BLITZ;
            case "ST" -> SPEED_TYPER;
            case "WS" -> WORD_SCRAMBLE;
            case "MW" -> MATCH_WORD;
            case "SU" -> SENTENCE_USAGE;
            default -> LEARN_MISSING_CHARACTERS;
        };
    }
}
