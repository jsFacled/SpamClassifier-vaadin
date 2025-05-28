package com.ml.spam.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class DatasetInspector {

    private static Map<String, Integer> missingValuesPerColumn = new LinkedHashMap<>();
    private static int totalRows = 0;
    private static String[] headers;

    private static Map<String, Integer> nonNumericPerColumn = new LinkedHashMap<>();

    private static Set<String> nonZeroValuesPerColumn = new HashSet<>();

    public static void mostrarResumenGeneral(String path) throws Exception {
        missingValuesPerColumn.clear();
        nonNumericPerColumn.clear();
        nonZeroValuesPerColumn.clear();
        totalRows = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new Exception("El archivo está vacío.");

            headers = headerLine.split(",");
            for (String h : headers) {
                missingValuesPerColumn.put(h, 0);
                nonNumericPerColumn.put(h, 0);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                totalRows++;

                for (int i = 0; i < headers.length; i++) {
                    String value = (i < parts.length) ? parts[i].trim() : "";
                    String colName = headers[i];

                    if (value.isEmpty() || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("null")) {
                        missingValuesPerColumn.put(colName, missingValuesPerColumn.get(colName) + 1);
                    } else {
                        try {
                            float num = Float.parseFloat(value);
                            if (num != 0.0f) {
                                nonZeroValuesPerColumn.add(colName);
                            }
                        } catch (NumberFormatException e) {
                            nonNumericPerColumn.put(colName, nonNumericPerColumn.get(colName) + 1);
                        }
                    }
                }
            }

            System.out.println(">> Total de filas: " + totalRows);
            System.out.println(">> Total de columnas: " + headers.length);
            System.out.println(">> Valores faltantes por columna:");
            missingValuesPerColumn.forEach((k, v) -> System.out.println(" - " + k + ": " + v));

            System.out.println(">> Valores no numéricos por columna:");
            nonNumericPerColumn.forEach((k, v) -> {
                if (v > 0) System.out.println(" - " + k + ": " + v);
            });

            List<String> columnasTodoCero = new ArrayList<>();
            for (String col : headers) {
                if (!nonZeroValuesPerColumn.contains(col)) {
                    columnasTodoCero.add(col);
                }
            }

            System.out.println(">> Columnas con solo ceros: " + columnasTodoCero.size());
            if (!columnasTodoCero.isEmpty()) {
                System.out.println("Columnas con solo ceros:");
                columnasTodoCero.forEach(col -> System.out.println(" - " + col));
            }

            StringBuilder resumen = new StringBuilder();
            resumen.append("Total de filas: ").append(totalRows).append("\n");
            resumen.append("Total de columnas: ").append(headers.length).append("\n");

            if (missingValuesPerColumn.values().stream().allMatch(v -> v == 0)) {
                resumen.append("No hay valores faltantes.\n");
            } else {
                resumen.append("Hay valores faltantes.\n");
            }

            if (columnasTodoCero.isEmpty()) {
                resumen.append("Todas las columnas contienen al menos un valor distinto de cero.\n");
            } else {
                resumen.append("Hay columnas con solo ceros: ").append(columnasTodoCero.size()).append("\n");
                resumen.append("Columnas:\n");
                columnasTodoCero.forEach(col -> resumen.append(" - ").append(col).append("\n"));
            }

            ChartLauncher.launchResumenConTexto(missingValuesPerColumn, resumen.toString());
        }
    }

    public static void mostrarHistograma(String path, String columnName) throws Exception {
        List<Double> values = leerColumnaNumerica(path, columnName);
        if (values.isEmpty()) {
            System.out.println("[INFO] No se encontraron valores numéricos válidos en la columna.");
            return;
        }
        ChartLauncher.launchHistogram(columnName, values);
    }

    public static void mostrarBoxplot(String path, String columnName) throws Exception {
        List<Double> values = leerColumnaNumerica(path, columnName);
        if (values.isEmpty()) {
            System.out.println("[INFO] No se encontraron valores numéricos válidos.");
            return;
        }
        ChartLauncher.launchBoxplot(columnName, values);
    }

    public static void calcularCorrelacionManual(String path, String col1, String col2) throws Exception {
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        int index1 = -1, index2 = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String[] headers = reader.readLine().split(",");
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals(col1)) index1 = i;
                if (headers[i].equals(col2)) index2 = i;
            }
            if (index1 == -1 || index2 == -1) throw new Exception("Una o ambas columnas no fueron encontradas.");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                try {
                    x.add(Double.parseDouble(parts[index1].trim()));
                    y.add(Double.parseDouble(parts[index2].trim()));
                } catch (Exception ignored) {}
            }
        }

        if (x.size() != y.size() || x.isEmpty()) {
            System.out.println("[INFO] No hay suficientes datos válidos para calcular la correlación.");
            return;
        }

        double meanX = x.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double meanY = y.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double sumNum = 0, sumDenX = 0, sumDenY = 0;
        for (int i = 0; i < x.size(); i++) {
            double dx = x.get(i) - meanX;
            double dy = y.get(i) - meanY;
            sumNum += dx * dy;
            sumDenX += dx * dx;
            sumDenY += dy * dy;
        }

        double r = sumNum / Math.sqrt(sumDenX * sumDenY);
        System.out.printf(">> Correlación de Pearson entre '%s' y '%s': %.4f\n", col1, col2, r);
    }

    public static Map<String, Integer> getMissingValuesPerColumn() {
        return missingValuesPerColumn;
    }

    private static List<Double> leerColumnaNumerica(String path, String columnName) throws Exception {
        List<Double> values = new ArrayList<>();
        int colIndex = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String[] headers = reader.readLine().split(",");
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals(columnName)) {
                    colIndex = i;
                    break;
                }
            }
            if (colIndex == -1) throw new Exception("Columna no encontrada: " + columnName);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (colIndex < parts.length) {
                    try {
                        values.add(Double.parseDouble(parts[colIndex].trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return values;
    }
}
