package com.ml.spam.datasetProcessor.models;

public enum DatasetColumnName {
    // Features por palabra strongSpamWord (prefijo + palabra dinámica)
    FREQ("freq_"),                        // Ej: freq_lexactions
    RELATIVE_FREQ_NORM("relativeFreqNorm_"), // Ej: relativeFreqNorm_lexactions
    POLARITY("polarity_"),               // Ej: polarity_lexactions

    // Features globales (nombres fijos)
    COUNT_MODERATE_SPAM_WORDS("count_moderateSpamWords"),
    RATIO_MODERATE_SPAM_WORDS("ratio_moderateSpamWords"),
    COUNT_WEAK_SPAM_WORDS("count_weakSpamWords"),
    RATIO_WEAK_SPAM_WORDS("ratio_weakSpamWords"),
    COUNT_HAM_INDICATORS("count_hamIndicators"),
    RATIO_HAM_INDICATORS("ratio_hamIndicators"),
    COUNT_NEUTRAL_BALANCED_WORDS("count_neutralBalancedWords"),
    RATIO_NEUTRAL_BALANCED_WORDS("ratio_neutralBalancedWords"),
    COUNT_STOP_WORDS("count_stopWords"),
    RATIO_STOP_WORDS("ratio_stopWords"),
    COUNT_RARE_SYMBOLS("count_rareSymbols"),
    RATIO_RARE_SYMBOLS("ratio_rareSymbols"),
    COUNT_UNASSIGNED_WORDS("count_unassignedWords"),
    RATIO_UNASSIGNED_WORDS("ratio_unassignedWords"),
    MESSAGE_LENGTH("longitud_mensaje"),
    LEXICAL_DIVERSITY("lexical_diversity"),
    NET_WEIGHTED_SCORE("net_weighted_score"),
    LABEL("label");

    private final String columnName;

    DatasetColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String get() {
        return columnName;
    }
}
