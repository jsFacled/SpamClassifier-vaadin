package com.ml.spam.dictionary.models;

public enum LexemeDictionaryCategory {
    TEXT_LEXEMES("textLexemes"),
    NUM_LEXEMES("numLexemes"),
    CONTEXTUAL_LEXEMES("contextualLexemes");

    private final String jsonKey;

    LexemeDictionaryCategory(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }
}

