package com.ml.spam.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidades para procesar bloques de texto separados por triple comillas.
 */
public class TripleQuoteUtils {

    private static final Pattern TRIPLE_QUOTE_PATTERN =
            Pattern.compile("(?m)^[\"“”]{3}\\s*$([\\s\\S]*?)(?=^[\"“”]{3}\\s*$|\\z)", Pattern.MULTILINE);

    private TripleQuoteUtils() {
        // Clase de utilidades
    }

    /**
     * Extrae los mensajes contenidos entre bloques de triple comillas.
     *
     * @param content contenido completo del archivo o texto.
     * @return lista de mensajes sin líneas vacías.
     */
    public static List<String> extractMessages(String content) {
        List<String> messages = new ArrayList<>();
        if (content == null) {
            return messages;
        }
        Matcher matcher = TRIPLE_QUOTE_PATTERN.matcher(content);
        while (matcher.find()) {
            String message = matcher.group(1).trim();
            if (!message.isEmpty()) {
                messages.add(message);
            }
        }
        return messages;
    }
}