package com.ml.spam.utils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvUtils {

    /**
     * Detecta el delimitador utilizado en la primera línea de un archivo CSV.
     *
     * @param filePath Ruta del archivo CSV.
     * @return El delimitador detectado (por defecto una coma si no se encuentra otro patrón).
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static String detectDelimiter(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            if (firstLine != null) {
                if (firstLine.contains(",")) {
                    return ",";
                } else if (firstLine.contains(";")) {
                    return ";";
                } else if (firstLine.contains("\t")) {
                    return "\t";
                } else if (firstLine.contains(" ")) {
                    return " ";
                }
            }
        }
        return ","; // Por defecto, devuelve la coma
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
