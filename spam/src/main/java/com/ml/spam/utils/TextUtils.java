package com.ml.spam.utils;

import java.util.Set;

public class TextUtils {

    // Normalización de texto
    public static String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
    }

    // Verifica si la palabra es típica de spam
    public static boolean isSpamWord(String word) {
        Set<String> spamWords = Set.of("gratis", "descuento", "oferta", "compra", "urgente");
        return spamWords.contains(word.toLowerCase());
    }

    // Verifica si la palabra es una stop word
    public static boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("el", "la", "de", "y", "a", "que", "en", "un", "por");
        return stopWords.contains(word.toLowerCase());
    }

    // Determina si la palabra contiene símbolos raros
    public static boolean containsRareSymbols(String word) {
        return word.matches(".*[\\W_]+.*"); // Verifica si contiene caracteres no alfanuméricos
    }
}
