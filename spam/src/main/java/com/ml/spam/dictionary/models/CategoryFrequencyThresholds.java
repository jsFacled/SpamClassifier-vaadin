package com.ml.spam.dictionary.models;

/**
 * Define los umbrales de frecuencia y proporción ham/spam
 * para determinar la categoría de una palabra.
 */
public enum CategoryFrequencyThresholds {

    STRONG_SPAM(15, Integer.MAX_VALUE, 0.0, 0.40),
    MODERATE_SPAM(5, 14, 0.41, 0.60),
    WEAK_SPAM(2, 4, 0.61, 0.74),
    HAM_INDICATOR(1, Integer.MAX_VALUE, 0.75, 1.0);  // ahora requiere al menos 1 ham

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
