package com.ml.spam.utils;

public class TextUtils {
    public static String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
    }
}
