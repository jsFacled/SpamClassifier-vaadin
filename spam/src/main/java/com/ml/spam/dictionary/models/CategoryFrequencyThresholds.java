package com.ml.spam.dictionary.models;

/**
 * Define los umbrales de frecuencia y proporción ham/spam
 * para determinar la categoría de una palabra.
 */
public enum CategoryFrequencyThresholds {

    HAM_INDICATOR(3, Integer.MAX_VALUE, 0, 60, 1.3),
    STRONG_SPAM(0, 0, 8, Integer.MAX_VALUE, Double.POSITIVE_INFINITY),
    MODERATE_SPAM(0, 2, 4, Integer.MAX_VALUE, 4.0),
    WEAK_SPAM(0, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 1.5),
    NEUTRAL_BALANCED(2, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 1.5);  // Nueva categoría

    private final int minHam;
    private final int maxHam;
    private final int minSpam;
    private final int maxSpam;
    private final double ratio;

    CategoryFrequencyThresholds(int minHam, int maxHam, int minSpam, int maxSpam, double ratio) {
        this.minHam = minHam;
        this.maxHam = maxHam;
        this.minSpam = minSpam;
        this.maxSpam = maxSpam;
        this.ratio = ratio;
    }

    public int getMinHam() { return minHam; }
    public int getMaxHam() { return maxHam; }
    public int getMinSpam() { return minSpam; }
    public int getMaxSpam() { return maxSpam; }
    public double getRatio() { return ratio; }
}
