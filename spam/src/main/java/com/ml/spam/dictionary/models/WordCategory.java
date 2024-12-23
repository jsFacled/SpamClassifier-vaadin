package com.ml.spam.dictionary.models;

public enum WordCategory {
    SPAM_WORDS("spamWords"),          // Palabras relacionadas con spam
    RARE_SYMBOLS("rareSymbols"),      // Símbolos raros
    STOP_WORDS("stopWords"),          // Palabras irrelevantes
    UNASSIGNED_WORDS("unassignedWords"); // Palabras nuevas NO ASIGNADAS TODAVÍA

    private final String jsonKey;

    WordCategory(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }
}
