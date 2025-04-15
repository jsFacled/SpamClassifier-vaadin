package com.ml.spam.dictionary.models;

public enum WordCategory {
    STRONG_SPAM_WORD("strongSpamWord", 3),
    MODERATE_SPAM_WORD("moderateSpamWord", 2),
    WEAK_SPAM_WORD("weakSpamWord", 1),
    HAM_INDICATORS("hamIndicators", 0.5),
    STOP_WORDS("stopWords", 0),
    RARE_SYMBOLS("rareSymbols", 0.5),
    NEUTRAL_BALANCED_WORD("neutralBalancedWords", 0.0),  // Nueva categoría
    UNASSIGNED_WORDS("unassignedWords", 0);

    private final String name;
    private final double weight;

    WordCategory(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }
}
