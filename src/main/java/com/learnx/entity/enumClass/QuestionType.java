package com.learnx.entity.enumClass;

public enum QuestionType {
    SINGLE_CHOICE("Single Choice"),
    MULTIPLE_CHOICE("Multiple Choice"),
    TRUE_FALSE("True/False"),
    FILL_IN_THE_BLANK("Fill in the Blank"),
    MATCHING("Matching");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
