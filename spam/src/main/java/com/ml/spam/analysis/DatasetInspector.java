package com.ml.spam.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class DatasetInspector {

    private static Map<String, Integer> missingValuesPerColumn = new LinkedHashMap<>();
    private static int totalRows = 0;
    private static String[] headers;

    public static void mostrarResumenGeneral(String path) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new Exception("El archivo está vacío.");

            headers = headerLine.split(",");
            for (String h : headers) missingValuesPerColumn.put(h, 0);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                totalRows++;

                for (int i = 0; i < headers.length; i++) {
                    if (i >= parts.length || parts[i].trim().isEmpty()
                            || parts[i].equalsIgnoreCase("NaN")
                            || parts[i].equalsIgnoreCase("null")) {
                        String colName = headers[i];
                        missingValuesPerColumn.put(colName, missingValuesPerColumn.get(colName) + 1);
                    }
                }
            }

            System.out.println(">> Total de filas: " + totalRows);
            System.out.println(">> Total de columnas: " + headers.length);
            System.out.println(">> Valores faltantes por columna:");
            missingValuesPerColumn.forEach((k, v) -> System.out.println(" - " + k + ": " + v));

            ChartLauncher.launchResumen(missingValuesPerColumn);

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
