package com.ml.spam.utils;

import com.ml.spam.datasetProcessor.models.RowValidationResult;
import com.ml.spam.dictionary.models.MessageLabel;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TextUtils {

    public static RowValidationResult validateAndNormalizeRow(String row) {
        if (row == null || row.trim().isEmpty()) {
            return new RowValidationResult(false, null, null);
        }

        // Dividir por coma
        String[] parts = row.split(",");
        if (parts.length != 2) {
            return new RowValidationResult(false, null, null); // Fila inválida
        }

        // Normalizar mensaje y etiqueta
        String message = parts[0].trim();
        String label = parts[1].trim();

        // Validar mensaje y etiqueta
        if (message.isEmpty() || (!label.equalsIgnoreCase("spam") && !label.equalsIgnoreCase("ham"))) {
            return new RowValidationResult(false, null, null);
        }

        // Retornar la fila normalizada como válida
        return new RowValidationResult(true, message, label);
    }


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
        try {
            MessageLabel.valueOf(value.toUpperCase()); // Verifica si existe en el enum
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isValidMessageAndLabel(String[] row) {
        if (row == null || row.length != 2) {
            return false; // Estructura incorrecta
        }

        String message = row[0].trim();
        String label = row[1].trim();

        if (message.isEmpty()) {
            return false;
        }

        try {
            MessageLabel.valueOf(label.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }


    public static List<String> tokenizeMessage(String message) {
        return Arrays.asList(message.toLowerCase().split("\\s+"));
    }

}
