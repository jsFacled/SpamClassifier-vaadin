package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.datasetProcessor.models.DatasetRow;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DatasetExporter {

    /**
     * Exporta un dataset a un archivo CSV con encabezado y filas ordenadas.
     *
     * @param datasetRows Lista de filas del dataset.
     * @param header Lista de nombres de columnas (ordenadas).
     * @param outputPath Ruta de salida del archivo CSV.
     */
    public static void exportDataset(List<DatasetRow> datasetRows, List<String> header, String outputPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            // Escribir encabezado
            writer.write(String.join(",", header));
            writer.newLine();

            // Escribir cada fila
            for (DatasetRow row : datasetRows) {
                List<Double> values = row.getValuesOrdered(header);
                String line = values.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");
                writer.write(line);
                writer.newLine();
            }

            System.out.println("[INFO] Dataset CSV exportado correctamente a: " + outputPath);

        } catch (IOException e) {
            throw new RuntimeException("Error al exportar el dataset: " + e.getMessage(), e);
        }
    }
}
