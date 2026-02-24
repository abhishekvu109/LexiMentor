package com.abhi.leximentor.leximentor.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChallengeType {

    LEARN_MEANING("MEANING"),
    LEARNING_SPELLINGS("SPELLINGS"),
    LEARN_POS("POS"),
    LEARN_PRONUNCIATION("PRONUNCIATION"),
    LEARN_MISSING_CHARACTERS("LMC"),
    IDENTIFY_WORD("IDENTIFY"),
    GUESS_WORD("GUESS"),
    CONTEXT_MASTER("CM"),
    SPEED_TYPER("ST"),
    FLASHCARD_BLITZ("FB"),
    WORD_SCRAMBLE("WS"),
    MATCH_WORD("MW"),
    SENTENCE_USAGE("SU");

    private final String value;

    public static ChallengeType of(String challengeType) {
        return switch (challengeType.toUpperCase()) {
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

    public static String of(ChallengeType challengeType) {
        return switch (challengeType) {
            case LEARN_MEANING -> LEARN_MEANING.value;
            case LEARNING_SPELLINGS -> LEARNING_SPELLINGS.value;
            case LEARN_POS -> LEARN_POS.value;
            case LEARN_PRONUNCIATION -> LEARN_PRONUNCIATION.value;
            case IDENTIFY_WORD -> IDENTIFY_WORD.value;
            case GUESS_WORD -> GUESS_WORD.value;
            case CONTEXT_MASTER -> CONTEXT_MASTER.value;
            case FLASHCARD_BLITZ -> FLASHCARD_BLITZ.value;
            case SPEED_TYPER -> SPEED_TYPER.value;
            case WORD_SCRAMBLE -> WORD_SCRAMBLE.value;
            case MATCH_WORD -> MATCH_WORD.value;
            case SENTENCE_USAGE -> SENTENCE_USAGE.value;
            default -> LEARN_MISSING_CHARACTERS.value;
        };
    }
}
