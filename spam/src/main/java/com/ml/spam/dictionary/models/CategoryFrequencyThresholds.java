package com.ml.spam.dictionary.models;

public enum CategoryFrequencyThresholds {
    STRONG_SPAM_MIN(15),      // Mínimo para strong spam
    MODERATE_SPAM_MIN(5),     // Mínimo para moderate spam
    MODERATE_SPAM_MAX(14),    // Máximo para moderate spam
    WEAK_SPAM_MIN(2),         // Mínimo para weak spam
    WEAK_SPAM_MAX(4);         // Máximo para weak spam

    private final int value;

    CategoryFrequencyThresholds(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
