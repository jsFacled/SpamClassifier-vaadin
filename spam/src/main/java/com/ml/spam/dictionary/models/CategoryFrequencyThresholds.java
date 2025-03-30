package com.ml.spam.dictionary.models;

/**
 * Define los umbrales de frecuencia y proporción ham/spam
 * para determinar la categoría de una palabra.
 */
public enum CategoryFrequencyThresholds {

    STRONG_SPAM(15, Integer.MAX_VALUE, 0.0, 0.25),
    MODERATE_SPAM(5, 14, 0.26, 0.50),
    WEAK_SPAM(2, 4, 0.51, 0.74),
    HAM_INDICATOR(0, Integer.MAX_VALUE, 0.75, 1.0);

    private final int minFrequency;
    private final int maxFrequency;
    private final double minHamRatio;
    private final double maxHamRatio;

    CategoryFrequencyThresholds(int minFrequency, int maxFrequency, double minHamRatio, double maxHamRatio) {
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.minHamRatio = minHamRatio;
        this.maxHamRatio = maxHamRatio;
    }

    public int getMinFrequency() {
        return minFrequency;
    }

    public int getMaxFrequency() {
        return maxFrequency;
    }

    public double getMinHamRatio() {
        return minHamRatio;
    }

    public double getMaxHamRatio() {
        return maxHamRatio;
    }
}
