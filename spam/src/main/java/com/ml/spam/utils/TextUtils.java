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

    // Valida si una fila es válida para el dataset
    public static boolean isRawRow(String[] row) {
        // Regla 1: La fila debe tener exactamente 2 columnas
        if (row == null || row.length != 2) {
            return false;
        }

        // Regla 2: La segunda columna debe ser 'spam' o 'ham'
        return isSpamOrHam(row[1]);
    }

    // Verifica si un valor es 'spam' o 'ham'
    public static boolean isSpamOrHam(String value) {
        return "spam".equalsIgnoreCase(value) || "ham".equalsIgnoreCase(value);
    }
}
