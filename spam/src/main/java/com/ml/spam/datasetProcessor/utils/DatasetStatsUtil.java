package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.dictionary.models.MessageLabel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidades para obtener estadísticas básicas de archivos de mensajes.
 * Permite contar filas, etiquetas y duplicados utilizando {@link DuplicateMessageChecker}.
 */
public class DatasetStatsUtil {

    /**
     * Cuenta la cantidad de mensajes de un archivo según su formato.
     *
     * @param input  ruta del archivo a analizar
     * @param format formato de entrada
     * @return cantidad de mensajes encontrados
     */
    public static long countRows(Path input, DuplicateMessageChecker.InputFormat format) throws IOException {
        if (format == DuplicateMessageChecker.InputFormat.LINE_BY_LINE) {
            try (BufferedReader reader = Files.newBufferedReader(input)) {
                return reader.lines().count();
            }
        } else if (format == DuplicateMessageChecker.InputFormat.TRIPLE_QUOTED) {
            String content = Files.readString(input);
            Pattern pattern = Pattern.compile("(?m)^\"\"\"\\s*$([\\s\\S]*?)(?=^\"\"\"\\s*$|\\z)", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(content);
            long count = 0;
            while (matcher.find()) {
                String msg = matcher.group(1).trim();
                if (!msg.isEmpty()) {
                    count++;
                }
            }
            return count;
        }
        throw new IllegalArgumentException("Formato no soportado: " + format);
    }

    /**
     * Cuenta cuántos mensajes pertenecen a cada etiqueta dentro de un archivo.
     *
     * @param input  ruta del archivo a analizar
     * @param format formato de entrada
     * @return mapa con el total por {@link MessageLabel}
     */
    public static Map<MessageLabel, Long> countLabels(Path input, DuplicateMessageChecker.InputFormat format) throws IOException {
        Map<MessageLabel, Long> result = new EnumMap<>(MessageLabel.class);
        for (MessageLabel label : MessageLabel.values()) {
            result.put(label, 0L);
        }

        if (format == DuplicateMessageChecker.InputFormat.LINE_BY_LINE) {
            try (BufferedReader reader = Files.newBufferedReader(input)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int comma = line.lastIndexOf(',');
                    if (comma > 0) {
                        String lbl = line.substring(comma + 1).trim().toLowerCase();
                        for (MessageLabel ml : MessageLabel.values()) {
                            if (ml.getKey().equals(lbl)) {
                                result.put(ml, result.get(ml) + 1);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (format == DuplicateMessageChecker.InputFormat.TRIPLE_QUOTED) {
            String content = Files.readString(input);
            Pattern pattern = Pattern.compile("(?m)^\"\"\"\\s*$([\\s\\S]*?)(?=^\"\"\"\\s*$|\\z)", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String block = matcher.group(1).trim();
                int comma = block.lastIndexOf(',');
                if (comma > 0) {
                    String lbl = block.substring(comma + 1).trim().toLowerCase();
                    for (MessageLabel ml : MessageLabel.values()) {
                        if (ml.getKey().equals(lbl)) {
                            result.put(ml, result.get(ml) + 1);
                            break;
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Formato no soportado: " + format);
        }

        return result;
    }

    /**
     * Calcula la cantidad de mensajes duplicados en un archivo.
     *
     * @param input  ruta del archivo a analizar
     * @param format formato de entrada
     * @return cantidad de mensajes duplicados
     */
    public static int countDuplicates(Path input, DuplicateMessageChecker.InputFormat format) throws IOException {
        DuplicateMessageChecker checker = new DuplicateMessageChecker();
        return checker.countDuplicates(input.toString(), format);
    }
}