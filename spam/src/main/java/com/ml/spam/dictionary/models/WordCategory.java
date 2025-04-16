package com.ml.spam.dictionary.models;

public enum WordCategory {
    STRONG_SPAM_WORD("strongSpamWord", 3),
    MODERATE_SPAM_WORD("moderateSpamWord", 2),
    WEAK_SPAM_WORD("weakSpamWord", 1),
    HAM_INDICATORS("hamIndicators", 0.5),
    STOP_WORDS("stopWords", 0),
    RARE_SYMBOLS("rareSymbols", 0.5),
    NEUTRAL_BALANCED_WORD("neutralBalancedWords", 0.0), // Nueva categoría
    UNASSIGNED_WORDS("unassignedWords", 0);

    private final String jsonKey;
    private final double weight;

    WordCategory(String jsonKey, double weight) {
        this.jsonKey = jsonKey;
        this.weight = weight;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public double getWeight() {
        return weight;
    }

    public static WordCategory fromJsonKey(String key) {
        for (WordCategory category : values()) {
            if (category.jsonKey.equalsIgnoreCase(key)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid WordCategory key: " + key);
    }
}
