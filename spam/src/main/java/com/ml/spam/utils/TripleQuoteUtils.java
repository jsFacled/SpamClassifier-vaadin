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

    /**
     * Patrón de expresión regular para encontrar bloques de texto
     * delimitados por tres comillas (\", \u201C, \u201D) al inicio y fin de línea.
     * Utiliza Pattern.MULTILINE para que '^' y '$' coincidan con los
     * inicios y finales de cada línea.
     * El grupo de captura (1) obtiene el contenido del mensaje.
     */
  //  private static final Pattern TRIPLE_QUOTE_PATTERN =
   //         Pattern.compile("^[\"“”]{3}\\s*$([\\s\\S]*?)(?=^[\"“”]{3}\\s*$|\\z)", Pattern.MULTILINE);

    // Bloques que inician y cierran con triple comilla doble o simple
   // private static final String QUOTE_BLOCK = "[\"“”]{3}|['‘’]{3}";

            //Reconoce ambos tipos de triplecomillas tanto si el mensaje está en distintos renglones como
            //en el mismo renglón. No se debe eliminar "?:"
    private static final String QUOTE_BLOCK = "(?:[\"\\u201C\\u201D]{3}|['\\u2018\\u2019]{3})";

    private static final Pattern TRIPLE_QUOTE_PATTERN =
            Pattern.compile(QUOTE_BLOCK + "\\s*(.*?)\\s*" + QUOTE_BLOCK, Pattern.DOTALL);


    /**
     * Constructor privado para evitar la instanciación de esta clase de utilidad.
     */
    private TripleQuoteUtils() {
        // Clase de utilidades
    }

    /**
     * Extrae todos los mensajes contenidos entre bloques de triples comillas
     * de una cadena de texto dada. Los mensajes extraídos se limpian de
     * espacios en blanco iniciales o finales y se omiten si están vacíos.
     *
     * @param content La cadena de texto completa de la cual extraer los mensajes.
     * Puede ser null.
     * @return Una lista de {@code String}s, donde cada String es un mensaje extraído.
     * Retorna una lista vacía si el contenido es null o no se encuentran mensajes.
     */
    public static List<String> extractMessages(String content) {
        List<String> messages = new ArrayList<>();

        if (content == null) {
            return messages; // Retorna una lista vacía si la entrada es nula.
        }

        Matcher matcher = TRIPLE_QUOTE_PATTERN.matcher(content);
        while (matcher.find()) {
            String message = matcher.group(1).trim(); // Obtiene el grupo capturado y elimina espacios.
            if (!message.isEmpty()) {
                messages.add(message); // Añade el mensaje si no está vacío.
            }
        }
        return messages;
    }
}