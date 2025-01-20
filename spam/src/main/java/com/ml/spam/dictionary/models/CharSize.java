package com.ml.spam.dictionary.models;

public enum CharSize {
    ONE_CHAR("oneChar", 1),
    TWO_CHARS("twoChars", 2),
    THREE_CHARS("threeChars", 3),
    FOUR_CHARS("fourChars", 4),
    FIVE_CHARS("fiveChars", 5),
    SIX_CHARS("sixChars", 6),
    SEVEN_CHARS("sevenChars", 7),
    EIGHT_CHARS("eightChars", 8),
    NINE_CHARS("nineChars", 9),
    TEN_CHARS("tenChars", 10),
    OVER_TEN_CHARS("overTenChars", -1); // Sin restricción

    private final String jsonKey;
    private final int size;

    CharSize(String jsonKey, int size) {
        this.jsonKey = jsonKey;
        this.size = size;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public int getSize() {
        return size;
    }
}
