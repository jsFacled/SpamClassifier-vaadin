package com.ml.spam.utils;

import com.ml.spam.datasetProcessor.models.RowValidationResult;
import com.ml.spam.dictionary.models.MessageLabel;

import java.util.ArrayList;
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

    /**
     * Valida si una fila cumple con los requisitos de formato y contenido para procesamiento.
     *
     * Este métod verifica que:
     * 1. La fila no sea nula y tenga exactamente dos columnas.
     * 2. El mensaje (primer columna) no esté vacío.
     * 3. El label (segunda columna) sea un valor válido definido en el enum `MessageLabel`.
     *
     * @param row Arreglo de cadenas que representa una fila del dataset (mensaje y label).
     * @return true si la fila es válida; false en caso contrario.
     */
    public static boolean isValidMessageAndLabel(String[] row) {
        // Validar que la fila no sea nula y tenga exactamente dos elementos
        if (row == null || row.length != 2) {
            return false; // Estructura incorrecta
        }

        // Eliminar espacios extra en los extremos de los elementos de la fila
        String message = row[0].trim();
        String label = row[1].trim();

        // Verificar que el mensaje no esté vacío
        if (message.isEmpty()) {
            return false; // Mensaje vacío
        }

        // Validar que el label sea un valor válido del enum `MessageLabel`
        try {
            MessageLabel.valueOf(label.toUpperCase()); // Convierte el label a mayúsculas para validar
        } catch (IllegalArgumentException e) {
            return false; // Label no coincide con ningún valor válido en `MessageLabel`
        }

        // Si pasa todas las validaciones, la fila es válida
        return true;
    }


    public static List<String> tokenizeMessage(String message) {
        return Arrays.asList(message.toLowerCase().split("\\s+"));
    }

    // Extraer palabras principales de los tokens
    public static List<String> extractWordsFromTokens(List<String> tokens) {

        List<String> words = new ArrayList<>();
        for (String token : tokens) {
            String word = token.replaceAll("[^\\p{L}áéíóúÁÉÍÓÚñÑ]", ""); // Solo letras
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return words;
    }

    // Extraer símbolos raros de los tokens usando containsRareSymbols
    public static List<String> extractRareSymbols(List<String> tokens) {
        List<String> symbols = new ArrayList<>();
        for (String token : tokens) {
            if (containsRareSymbols(token)) {
                // Extraer los símbolos raros del token
                String rareSymbols = token.replaceAll("[\\p{L}áéíóúÁÉÍÓÚñÑ]", ""); // No letras
                if (!rareSymbols.isEmpty()) {
                    symbols.add(rareSymbols);
                }
            }
        }
        return symbols;
    }

    /**
     * Divide un token en dos partes: la palabra principal y los símbolos raros.
     *
     * @param token El token a dividir.
     * @return Un arreglo de dos elementos: [palabra, símbolo raro].
     */
    /**
     * Divide un token en dos partes: la palabra principal y los símbolos raros.
     * Palabras válidas incluyen acentos y caracteres especiales del idioma español.
     *
     * @param token El token a dividir.
     * @return Un arreglo de dos elementos: [palabra, símbolo raro].
     */
    public static String[] splitRareSymbolsAndNumbers(String token) {
        // Extraer parte de palabras (letras con soporte para acentos)
        String wordPart = token.replaceAll("[^\\p{L}áéíóúÁÉÍÓÚñÑ]", ""); // Solo letras
        // Extraer números
        String numberPart = token.replaceAll("[^0-9]", ""); // Solo números
        // Extraer símbolos raros
        String rareSymbolsPart = token.replaceAll("[\\p{L}áéíóúÁÉÍÓÚñÑ0-9]", ""); // No letras ni números

        return new String[]{wordPart, numberPart, rareSymbolsPart};
    }
}
