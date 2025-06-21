package com.ml.spam.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase de utilidad para extraer bloques de texto delimitados por triples comillas.
 * Permite manejar diferentes tipos de comillas (simples o tipográficas)
 * y asegura una extracción limpia de los mensajes.
 */
public final class TripleQuoteUtils {

    // Reconoce triple comillas dobles o simples, incluyendo tipográficas
    private static final String QUOTE_BLOCK = "(?:[\"\\u201C\\u201D]{3}|['\\u2018\\u2019]{3})";

    // Patrón que reconoce bloques multilínea y de una sola línea
    private static final Pattern TRIPLE_QUOTE_PATTERN = Pattern.compile(
            QUOTE_BLOCK + "\\s*\\n([\\s\\S]*?)\\n" + QUOTE_BLOCK + "|" + // bloque multilínea
                    QUOTE_BLOCK + "(.*?)" + QUOTE_BLOCK,                         // bloque en una sola línea
            Pattern.MULTILINE
    );

    private TripleQuoteUtils() {
        // Clase de utilidades
    }

    /**
     * Extrae todos los mensajes contenidos entre bloques de triples comillas.
     *
     * @param content La cadena de texto completa de la cual extraer los mensajes.
     * @return Una lista de Strings, donde cada uno es un mensaje extraído y limpio.
     */
    public static List<String> extractMessages(String content) {
        List<String> messages = new ArrayList<>();

        if (content == null) return messages;

        Matcher matcher = TRIPLE_QUOTE_PATTERN.matcher(content);
        while (matcher.find()) {
            String message = matcher.group(1) != null ? matcher.group(1).trim() : matcher.group(2).trim();
            if (!message.isEmpty()) {
                messages.add(message);
            }
        }

        return messages;
    }
}
