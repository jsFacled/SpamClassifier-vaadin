package com.ml.spam.dictionary.models;
public enum CategoryFrequencyThresholds {
    STRONG_SPAM_MIN(9),
    MODERATE_SPAM_MIN(4),
    MODERATE_SPAM_MAX(8),
    WEAK_SPAM_MIN(2),
    WEAK_SPAM_MAX(3);

    private final int value;

    CategoryFrequencyThresholds(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
