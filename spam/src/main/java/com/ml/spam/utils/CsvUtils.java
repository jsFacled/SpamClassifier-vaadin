package com.ml.spam.utils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvUtils {

    // Detecta si una fila es una cabecera
    public static boolean isHeaderRow(String[] row) {
        for (String value : row) {
            if (value.matches("(?i)spam|ham|message|id|content|mensaje|tipo")) { // Palabras clave comunes de cabeceras
                return true;
            }
        }
        return false;
    }


    /**
     * Detecta el delimitador utilizado en un archivo CSV basado en el análisis de las primeras líneas.
     *
     * Este método analiza las primeras líneas del archivo para determinar cuál de los delimitadores
     * comunes (",", ";", "\t", " ") es más consistente al dividir las columnas. Valida que el delimitador
     * seleccionado produzca al menos un número mínimo de columnas esperadas (por defecto, 2: mensaje y etiqueta).
     *
     * @param filePath Ruta al archivo CSV.
     * @return El delimitador detectado como una cadena (por ejemplo, "," o ";").
     * @throws IOException Si el archivo está vacío, no se detecta un delimitador válido,
     *                     o ocurre un error al leer el archivo.
     */
    public static String detectDelimiter(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> delimiters = Arrays.asList(",", ";", "\t", " ");
            int expectedColumns = 2; // Se espera al menos mensaje y etiqueta
            int maxLines = 5; // Analizar las primeras 5 líneas

            String line;
            int lineCount = 0;

            // Contadores para cada delimitador
            Map<String, Integer> delimiterCounts = new HashMap<>();
            delimiters.forEach(delimiter -> delimiterCounts.put(delimiter, 0));

            while ((line = reader.readLine()) != null && lineCount < maxLines) {
                for (String delimiter : delimiters) {
                    String[] columns = line.split(delimiter);
                    if (columns.length >= expectedColumns) {
                        delimiterCounts.put(delimiter, delimiterCounts.get(delimiter) + 1);
                    }
                }
                lineCount++;
            }

            // Encontrar el delimitador más frecuente
            return delimiterCounts.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .filter(entry -> entry.getValue() > 0) // Validar que haya coincidencias
                    .map(Map.Entry::getKey)
                    .orElseThrow(() -> new IOException("No se pudo detectar un delimitador válido en las primeras líneas."));
        }
    }


    public static void removeHeaderIfPresent(List<String[]> rows) {
        if (!rows.isEmpty()) {
            String[] firstRow = rows.get(0);
            if (firstRow.length >= 2 &&
                    ((firstRow[0].equalsIgnoreCase("mensaje") || firstRow[0].equalsIgnoreCase("message")) &&
                            (firstRow[1].equalsIgnoreCase("tipo") || firstRow[1].equalsIgnoreCase("label")))) {
                rows.remove(0); // Eliminar cabecera
            }
        }
    }

    public static List<String> tokenizeMessage(String message) {
        return Arrays.asList(message.toLowerCase().split("\\s+"));
    }

    public static boolean isValidRow(String[] row) {
        return row != null && row.length >= 2;
    }




    /**
     * Detecta y muestra el delimitador del archivo CSV en la consola.
     *
     * @param filePath Ruta del archivo CSV.
     */
    public static void displayDelimiterInConsole(String filePath) {
        try {
            String delimiter = detectDelimiter(filePath);
            System.out.println("Delimitador detectado: \"" + delimiter + "\"");
        } catch (IOException e) {
            System.err.println("Error al detectar el delimitador: " + e.getMessage());
        }
    }

    /**
     * Detecta el delimitador utilizado en una línea específica.
     *
     * @param line Línea de texto del archivo CSV.
     * @return El delimitador detectado (por defecto una coma si no se encuentra otro patrón).
     */
    public static String detectDelimiterFromLine(String line) {
        if (line.contains(",")) {
            return ",";
        } else if (line.contains(";")) {
            return ";";
        } else if (line.contains("\t")) {
            return "\t";
        } else if (line.contains(" ")) {
            return " ";
        }
        return ","; // Por defecto
    }
}
