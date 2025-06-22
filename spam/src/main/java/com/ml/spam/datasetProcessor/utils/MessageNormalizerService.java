package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.dictionary.models.MessageLabel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Servicio de utilidad para normalizar mensajes ya cargados desde archivos.
 * Procesa contenido en bloques o líneas con etiquetas, eliminando duplicados y comillas.
 */
public class MessageNormalizerService {

    /**
     * Normaliza una lista de bloques (mensajes) provenientes de archivos triple comilla.
     * Resultado: mensaje limpio, una línea por mensaje, con etiqueta.
     *
     * @param blocks lista de bloques extraídos de un archivo
     * @param label etiqueta a añadir (spam o ham)
     * @return lista de mensajes únicos ya etiquetados
     */
    public List<String> normalizeFromTripleQuoteBlocks(List<String> blocks, MessageLabel label) {
        Set<String> unique = new LinkedHashSet<>();
        for (String block : blocks) {
            String clean = block.replaceAll("\\s+", " ").trim();
            if (!clean.isEmpty()) {
                unique.add(clean + "," + label.getKey());
            }
        }
        return new ArrayList<>(unique);
    }

    /**
     * Normaliza mensajes labeled ya leídos desde archivo CSV por ResourcesHandler.
     * Unifica espacios internos y devuelve mensaje,label por línea.
     *
     * @param rawRows lista de arreglos [mensaje, label]
     * @return lista de mensajes únicos con etiqueta incluida
     */
    public List<String> normalizeFromCsv(List<String[]> rawRows) {
        Set<String> unique = new LinkedHashSet<>();
        for (String[] row : rawRows) {
            if (row.length != 2) continue;
            String message = row[0].replaceAll("\\s+", " ").trim();
            String label = row[1].trim();
            if (!message.isEmpty() && !label.isEmpty()) {
                unique.add(message + "," + label);
            }
        }
        return new ArrayList<>(unique);
    }

    /**
     * Elimina las etiquetas "spam" o "ham" del final de cada línea, si existen.
     *
     * @param messages lista de mensajes etiquetados
     * @return lista de mensajes sin etiqueta
     */
    public List<String> removeLabels(List<String> messages) {
        List<String> result = new ArrayList<>();
        for (String line : messages) {
            int lastComma = line.lastIndexOf(',');
            if (lastComma > 0) {
                String possible = line.substring(lastComma + 1).trim().toLowerCase();
                if (MessageLabel.isValidKey(possible)) {
                    result.add(line.substring(0, lastComma).trim());
                    continue;
                }
            }
            result.add(line.trim());
        }
        return result;
    }

    /**
     * Exporta la lista de mensajes a un archivo de texto usando UTF-8.
     *
     * @param lines  lista de líneas a guardar
     * @param output ruta de salida
     */
    public void exportToFile(List<String> lines, Path output) throws IOException {
        Files.createDirectories(output.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
