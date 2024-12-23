package com.ml.spam.dictionary.models;

public enum LexemeRepositoryCategories {
    TEXT_LEXEMES("textLexemes"),
    NUM_LEXEMES("numLexemes"),
    CONTEXTUAL_LEXEMES("contextualLexemes");

    private final String jsonKey;

    LexemeRepositoryCategories(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }
}

