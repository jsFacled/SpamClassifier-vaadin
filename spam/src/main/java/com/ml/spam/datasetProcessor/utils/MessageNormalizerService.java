package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.dictionary.models.MessageLabel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Servicio de utilidad para normalizar archivos de mensajes en distintos formatos.
 * Permite leer archivos con mensajes en triple comilla o en CSV línea a línea,
 * eliminando comillas innecesarias y evitando duplicados.
 */
public class MessageNormalizerService {

    /**
     * Normaliza un archivo con mensajes encerrados entre triple comillas y agrega
     * la etiqueta indicada a cada mensaje.
     * Resultado: mensaje,label.
     * 
     * @param input ruta de entrada
     * @param label etiqueta a añadir (spam o ham)
     * @return lista de mensajes únicos ya etiquetados
     */
    public List<String> normalizeFromTripleQuotes(Path input, MessageLabel label) throws IOException {
        String content = Files.readString(input);
        String[] blocks = content.split("\"\"\"");

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
     * Normaliza un archivo CSV donde cada línea posee un mensaje y una etiqueta.
     * Quita comillas externas si existen y unifica espacios internos.
     *
     * @param input ruta de entrada
     * @return lista de mensajes únicos con etiqueta incluida
     */
    public List<String> normalizeFromCsv(Path input) throws IOException {
        Set<String> unique = new LinkedHashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(input)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int lastComma = line.lastIndexOf(',');
                if (lastComma <= 0) {
                    continue; // línea inválida
                }
                String message = line.substring(0, lastComma).trim();
                String label = line.substring(lastComma + 1).trim();

                if ((message.startsWith("\"") && message.endsWith("\"")) ||
                        (message.startsWith("'") && message.endsWith("'"))) {
                    message = message.substring(1, message.length() - 1).trim();
                }

                message = message.replaceAll("\\s+", " ");
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
