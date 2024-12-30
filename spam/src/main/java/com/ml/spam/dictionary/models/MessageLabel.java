package com.ml.spam.dictionary.models;


public enum MessageLabel {
    SPAM ("spam"),
    HAM ("ham");


    private final String key;

    MessageLabel(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static boolean isValidKey(String key) {
        for (MessageLabel messageLabel : values()) {
            if (messageLabel.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
