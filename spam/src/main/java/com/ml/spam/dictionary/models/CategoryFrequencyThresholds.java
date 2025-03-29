package com.ml.spam.dictionary.models;

/**
 * Define los umbrales mínimos de frecuencia absoluta y los rangos porcentuales
 * de proporción ham/total para asignar una categoría a una palabra.
 */
public enum CategoryFrequencyThresholds {

    /**
     * Palabras fuertemente asociadas al spam: aparecen al menos 15 veces
     * y tienen proporción de ham muy baja.
     */
    STRONG_SPAM(15, 0.0, 0.25),

    /**
     * Palabras moderadamente asociadas al spam: aparecen entre 5 y 14 veces
     * y no superan 49% de presencia ham.
     */
    MODERATE_SPAM(5, 0.0, 0.49),

    /**
     * Palabras débilmente asociadas al spam: al menos 2 apariciones y
     * proporción ham menor al 75%.
     */
    WEAK_SPAM(2, 0.0, 0.74),

    /**
     * Palabras indicadoras de ham: no se exige frecuencia mínima,
     * pero sí que 75% o más de sus apariciones sean ham.
     */
    HAM_INDICATOR(0, 0.75, 1.0);

    private final int minFrequency;     // Frecuencia mínima absoluta
    private final double minHamRatio;  // Proporción mínima de ham
    private final double maxHamRatio;  // Proporción máxima de ham

    CategoryFrequencyThresholds(int minFrequency, double minHamRatio, double maxHamRatio) {
        this.minFrequency = minFrequency;
        this.minHamRatio = minHamRatio;
        this.maxHamRatio = maxHamRatio;
    }

    public int getMinFrequency() {
        return minFrequency;
    }

    public double getMinHamRatio() {
        return minHamRatio;
    }

    public double getMaxHamRatio() {
        return maxHamRatio;
    }
}
