package com.ml.spam.dictionary.models;

public enum FrequencyKey {
    SPAM_FREQUENCY("spamFrequency"),
    HAM_FREQUENCY("hamFrequency");

    private final String key;

    FrequencyKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static boolean isValidKey(String key) {
        for (FrequencyKey frequencyKey : values()) {
            if (frequencyKey.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
