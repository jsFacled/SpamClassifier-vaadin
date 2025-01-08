package com.ml.spam.utils;

import com.ml.spam.datasetProcessor.models.RowValidationResult;
import com.ml.spam.dictionary.models.MessageLabel;
import com.ml.spam.dictionary.models.TokenType;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
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
            return Collections.emptyList();
        }
        // Normalizar mensaje: eliminar comillas y dividir por espacios
        return List.of(message.toLowerCase()
                .replaceAll("[\"']", "") // Elimina comillas dobles y simples
                .replace(",", "") // Opcional: elimina comas
                .trim()
                .split("\\s+"));
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
        if (isOneChar(token)) {
            return classifyTokenByOneDigit(token);
        }
        if (isTextNumSymbolToken(token)) {
            return TokenType.TEXT_NUM_SYMBOL; // Token tiene texto, números y símbolos mezclados
        }
        if (isWebEmail(token)) {
            return TokenType.TEXT_NUM_SYMBOL; // Clasifica correos como texto+número+símbolo
        }
        if (isWebAddress(token)) {
            return TokenType.TEXT_SYMBOL; // Clasifica URLs como texto+símbolo
        }
        if (isSymbolToken(token)) {
            return TokenType.SYMBOL; // Token es un símbolo
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
        return TokenType.UNASSIGNED; // Token no clasificable
    }

    public static boolean isOneChar(String token) {
        return token.length() == 1;    }

    public static boolean isTextToken(String token) {
        // Detecta palabras que contienen solo letras (incluye letras acentuadas y ñ automáticamente)
        return token.matches("\\p{L}+");
    }



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

    public static boolean isNumTextToken(String token) {
        return token.matches("(?=.*\\d)(?=.*[a-zA-Z]).*");
    }


    public static boolean isTextNumSymbolToken(String token) {
        // Verifica si el token contiene letras, números y símbolos al mismo tiempo
        return token.matches("^(?=.*\\p{L})(?=.*\\d)(?=.*\\W).+$");
    }

    public static boolean isWebAddress(String token) {
        return token.toLowerCase().matches("^(http|https)://.*\\.(com|net|org|edu|gov|mil|io|dev|site|online)$");
    }

    public static boolean isCharToken(String token) {
        return token.length() == 1 && Character.isLetter(token.charAt(0));
    }

    public static boolean isSymbolToken(String token) {
        // Detecta símbolos raros, pero excluye letras válidas
        return token.matches("[^\\p{L}\\d]+");
    }

    public static boolean containsEmoji(String token) {
        return token.codePoints().anyMatch(Character::isSupplementaryCodePoint);
    }

    /**
     * Verifica si un token es un horario en formato HH:MM o H:MM.
     *
     * @param token El token a verificar.
     * @return true si el token es un horario, false en caso contrario.
     */
    public static boolean isTime(String token) {
        return token.matches("^\\d{1,2}:\\d{2}$");
    }
    public static boolean isEmoji(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        // Expresión regular mejorada para detectar emojis
        return token.matches("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");
    }


    public static String[] splitNumberAndText(String token) {
        String[] parts = token.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
        if (parts.length > 2) {
            return new String[]{parts[0], String.join("", Arrays.copyOfRange(parts, 1, parts.length))};
        } else if (parts.length == 2) {
            return parts;
        } else {
            return new String[]{token, ""}; // Retorna el token completo si no se puede dividir
        }
    }


    public static String[] splitRareSymbolsAndNumbers(String token) {
        // Inicializar lista para componentes procesados
        List<String> components = new ArrayList<>();

        // Extraer letras, números y símbolos
        String wordPart = token.replaceAll("[^\\p{L}áéíóúÁÉÍÓÚñÑ]", ""); // Solo letras
        String numberPart = token.replaceAll("[^0-9]", ""); // Solo números
        String rareSymbolsPart = token.replaceAll("[\\p{L}áéíóúÁÉÍÓÚñÑ0-9]", ""); // Solo símbolos

        // Agregar partes no vacías
        if (!wordPart.isEmpty()) {
            components.add(wordPart);
        }
        if (!numberPart.isEmpty()) {
            components.add(numberPart);
        }
        if (!rareSymbolsPart.isEmpty()) {
            // Dividir símbolos en caracteres individuales
            for (char symbol : rareSymbolsPart.toCharArray()) {
                components.add(String.valueOf(symbol));
            }
        }

        // Convertir la lista a un arreglo
        return components.toArray(new String[0]);
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
        if (input == null || input.isEmpty()) {
            return false;
        }
        // Normalizar a forma NFD para separar los diacríticos
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Verificar si contiene caracteres de diacríticos (\p{M})
        return normalized.matches(".*\\p{M}.*");
    }

    public static String removeAccents(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String result = normalized.replaceAll("\\p{M}", "");
        System.out.println("[DEBUG] removeAccents: " + input + " -> " + result);
        return result;
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

    public static boolean isWebEmail(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return token.matches(emailRegex);
    }

}
