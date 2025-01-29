package com.ml.spam.dictionary.models;

//Lexemas que no están definidos en el repositorio. Se irán agregando a medida que se procesan los tokens.
public enum LexemeInProcess {

    HIGNUM("lexhignum");

    private final String jsonKey;

    LexemeInProcess(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }

}
