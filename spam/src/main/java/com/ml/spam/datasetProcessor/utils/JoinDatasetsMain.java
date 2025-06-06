package com.ml.spam.datasetProcessor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JoinDatasetsMain:
 * Clase utilitaria que unifica múltiples archivos CSV ya procesados
 * (datasets generados con features para Machine Learning) en un solo archivo combinado.
 *
 * Funcionalidad:
 * - Lee tres archivos CSV existentes con estructura idéntica.
 * - Verifica que todas las filas tengan la misma cantidad de columnas.
 * - Omite los encabezados de los archivos de entrada.
 * - Exporta un único archivo CSV con encabezado y todas las filas válidas combinadas.
 * - Muestra un informe por consola detallando columnas y cantidad de filas por archivo.
 *
 * Archivos involucrados (relativos a src/main/resources/):
 * - static/mlDatasets/generated_dataset_train.csv
 * - static/mlDatasets/generated_dataset_test.csv
 * - static/mlDatasets/generated_dataset_comillas_spam.csv
 *
 * Archivo de salida:
 * - static/combined_dataset.csv
 *
 * Uso: Ejecutar como clase Java principal (main) desde entorno de desarrollo o línea de comandos.
 */

public class JoinDatasetsMain {
    public static void main(String[] args) throws Exception {
        // Paths relativos y base
        String basePath = "spam/src/main/resources/";
      /*
        String file1 = "generated_dataset_train.csv";
        String file2 = "generated_dataset_test.csv";
        String file3 = "generated_dataset_comillas_spam.csv";
        String outputRelative = "static/mlDatasets/combined_dataset.csv";
        */

/*
        String file1 = "generated_dataset_ia_ham.csv";
        String file2 = "generated_dataset_ia_otros_spam_ham.csv";
        String file3 = "generated_dataset_ia_spam.csv";
        String outputRelative = "static/mlDatasets/combined_ia_tres_datasets.csv";
*/

        String file1 = "static/mlDatasets/combined_dataset.csv";
        String file2 = "static/mlDatasets/combined_ia_tres_datasets.csv";
        String outputRelative = "static/mlDatasets/mix_combined_full_dataset.csv";


        String outputAbsolute = basePath + outputRelative;

        // Leer encabezado
        String headerLine = Files.lines(Paths.get(basePath + file1))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se pudo leer el encabezado"));
        int expectedColumns = headerLine.split(",").length;

        // Reporte inicial
        int totalRows = 0;
        List<String[]> allRows = new ArrayList<>();
        for (String file : List.of(file1, file2)) {
            int rowsCount = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(basePath + file))) {
                String line = reader.readLine(); // omitir header
                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split(",");
                    if (columns.length != expectedColumns) {
                        throw new IllegalArgumentException("Fila con columnas incorrectas en " + file + ": " + line);
                    }
                    allRows.add(columns);
                    rowsCount++;
                }
            }
            totalRows += rowsCount;
            System.out.println("[INFO] " + file + " → columnas: " + expectedColumns + ", filas: " + rowsCount);
        }

        // Mezclar las filas combinadas
        Collections.shuffle(allRows);

        // Exportar
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputAbsolute))) {
            writer.write(headerLine);
            writer.newLine();
            for (String[] row : allRows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }

        System.out.println("[INFO] Exportación final → columnas: " + expectedColumns + ", filas totales: " + totalRows);
        System.out.println("[INFO] Archivo generado en: " + outputAbsolute);
    }
}
