package com.ml.spam.dictionary.models;


public enum CharSize {
    ONE_CHAR("oneChar"),
    TWO_CHARS("twoChars"),
    THREE_CHARS("threeChars"),
    FOUR_CHARS("fourChars"),
    FIVE_CHARS("fiveChars"),
    SIX_CHARS("sixChars"),
    SEVEN_CHARS("sevenChars"),
    EIGHT_CHARS("eightChars"),
    NINE_CHARS("nineChars"),
    TEN_CHARS("tenChars"),
    OVER_TEN_CHARS("overTenChars");

    private final String jsonKey;

    CharSize(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }
}
