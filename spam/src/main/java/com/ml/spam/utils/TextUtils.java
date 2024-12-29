package com.ml.spam.utils;

import com.ml.spam.datasetProcessor.models.RowValidationResult;
import com.ml.spam.dictionary.models.MessageLabel;
import com.ml.spam.dictionary.models.TokenType;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextUtils {

    // Normalización de texto

    public static String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
    }
    // Valida si una fila es válida para el dataset

    public static boolean isRawRow(String[] row) {
        if (row == null || row.length != 2) {
            return false;
        }
        return isSpamOrHam(row[1]);
    }
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
            return false;
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

    public static List<String> splitMessageAndLowercase(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Collections.emptyList(); // Retorna lista vacía si el mensaje es nulo o vacío
        }
        // Convierte a minúsculas, elimina comas y divide por espacios
        return List.of(message.toLowerCase().replace(",", "").trim().split("\\s+"));
    }



    /***************** Tratamiento con Tokens ***************************/


    public static TokenType classifyTokenByOneDigit(String token) {
       if (isCharToken(token)) {
                return TokenType.CHAR; // Token es un único carácter
            }
       if (isSymbolToken(token)) {
                return TokenType.SYMBOL; // Token es un símbolo raro puro
            }
        if (isNumericToken(token)) {
            return TokenType.NUM; // Token es un número puro
        }
        return TokenType.UNASSIGNED; // Token no clasificable
    }


    public static TokenType classifyToken(String token) {
        if (isOneDigit(token)){
            return classifyTokenByOneDigit(token);
        }

        if (isNumericToken(token)) {
            return TokenType.NUM; // Token es un número puro
        }
        if (isTextToken(token)) {
            return TokenType.TEXT; // Token es texto alfabético puro
        }
        if (isTextSymbolToken(token)) {
            return TokenType.TEXT_SYMBOL; // Token contiene texto y símbolos raros
        }
        if (isNumTextToken(token)) {
            return TokenType.NUM_TEXT; // Token contiene números y texto mezclados
        }
        if (isNumSymbolToken(token)) {
            return TokenType.NUM_SYMBOL; // Token contiene números y símbolos
        }
        if (isTextNumSymbolToken(token)) {
            return TokenType.TEXT_NUM_SYMBOL; // Token tiene texto, números y símbolos mezclados
        }
        if (isEmoji(token)) {
            return TokenType.SYMBOL; // Token es un emoji
        }
        return TokenType.UNASSIGNED; // Token no clasificable
    }

    public static boolean isOneDigit(String token) {
        return token.length() == 1;    }

    private static boolean isNumSymbolToken(String token) {
        return token.matches(".*\\d.*") && token.matches(".*\\W.*") && !token.matches(".*\\p{L}.*");
    }

    private static boolean isTextSymbolToken(String token) {
        // Verifica si el token contiene letras (\\p{L}) y símbolos (\\W) al mismo tiempo
        return token.matches(".*\\p{L}.*") && token.matches(".*\\W.*");
    }

    public static boolean isNumericToken(String token) {
        return token.matches("\\d+");
    }

    public static boolean isTextToken(String token) {
        return token.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+");
    }

    public static boolean isNumTextToken(String token) {
        return token.matches("\\d+[a-zA-Z]+|[a-zA-Z]+\\d+");
    }

    public static boolean isTextNumSymbolToken(String token) {
        return token.matches("^(?=.*[a-zA-Z])(?=.*\\\\d)(?=.*\\\\W).+$");
    }

    public static boolean isCharToken(String token) {
        return token.length() == 1 && Character.isLetter(token.charAt(0));
    }

    public static boolean isSymbolToken(String token) {
        return token.matches("[!@#$%^&*()_+={}:;\\\"',.<>?/-]+");
    }
    public static boolean isEmoji(String token) {
        // Verifica si el token contiene solo emojis (caracteres de la categoría Unicode 'So')
        return token != null && token.matches("[\\p{So}]+");
    }


    public static String[] splitNumberAndText(String token) {
        String numberPart = token.replaceAll("[^0-9]", "");
        String textPart = token.replaceAll("[0-9]", "");
        return new String[]{numberPart, textPart};
    }

    public static String[] splitRareSymbolsAndNumbers(String token) {
        String wordPart = token.replaceAll("[^\\p{L}áéíóúÁÉÍÓÚñÑ]", "");
        String numberPart = token.replaceAll("[^0-9]", "");
        String rareSymbolsPart = token.replaceAll("[\\p{L}áéíóúÁÉÍÓÚñÑ0-9]", "");
        if (!rareSymbolsPart.isEmpty()) {
            rareSymbolsPart = String.join(" ", rareSymbolsPart.split(""));
        }
        return new String[]{wordPart, numberPart, rareSymbolsPart};
    }


    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }
        String normalized = input.toLowerCase();
        normalized = normalized.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "");
        return normalized.trim();
    }

    public static String normalizeWithAccentRemoval(String input) {
        String normalized = normalizeString(input);
        if (normalized == null) {
            return null;
        }
        return Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public static boolean hasAccent(String input) {
        if (input == null) return false;
        return input.matches(".*[áéíóúÁÉÍÓÚñÑ].*");
    }

    public static String removeAccents(String input) {
        if (input == null) return null;
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public static boolean containsNumber(String token) {
        return token != null && token.matches(".*\\d.*");
    }

    public static List<String> cleanWords(List<String> words) {
        return words.stream()
                .map(TextUtils::normalize)
                .filter(word -> word != null && !word.isEmpty())
                .toList();
    }


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
}
