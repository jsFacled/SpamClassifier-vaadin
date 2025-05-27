package com.ml.spam.analysis;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class UnifiedChartApp {

    public static void mostrar(String tipo, String titulo, List<Double> valores, Map<String, Integer> resumenData) {
        Stage stage = new Stage();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);

        chart.setTitle(switch (tipo) {
            case "histogram", "boxplot" -> tipo.toUpperCase() + ": " + titulo;
            case "resumen" -> "Valores faltantes por columna";
            default -> "Gráfico";
        });

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Label infoLabel = new Label();

        switch (tipo) {
            case "resumen" -> {
                for (Map.Entry<String, Integer> entry : resumenData.entrySet()) {
                    if (entry.getValue() > 0)
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                if (series.getData().isEmpty()) {
                    infoLabel.setText("No hay valores faltantes en ninguna columna.");
                }
            }
            case "histogram" -> {
                Set<Double> unicos = new HashSet<>(valores);
                boolean sinVariacion = unicos.size() == 1;

                int buckets = 10;
                double min = Collections.min(valores);
                double max = Collections.max(valores);
                double step = (max - min) / buckets;
                if (step == 0) step = 1; // evita división por 0

                int[] counts = new int[buckets];
                for (double v : valores) {
                    int b = (int) Math.min((v - min) / step, buckets - 1);
                    counts[b]++;
                }
                for (int i = 0; i < buckets; i++) {
                    double from = min + i * step;
                    double to = from + step;
                    String label = String.format("[%.2f - %.2f)", from, to);
                    series.getData().add(new XYChart.Data<>(label, counts[i]));
                }
                if (sinVariacion) {
                    infoLabel.setText("Sin variación: todos los valores son iguales a " + min);
                }
            }
            case "boxplot" -> {
                Set<Double> unicos = new HashSet<>(valores);
                boolean sinVariacion = unicos.size() == 1;

                List<Double> sorted = valores.stream().sorted().toList();
                double q1 = percentile(sorted, 25);
                double med = percentile(sorted, 50);
                double q3 = percentile(sorted, 75);
                series.getData().add(new XYChart.Data<>("Min", sorted.get(0)));
                series.getData().add(new XYChart.Data<>("Q1", q1));
                series.getData().add(new XYChart.Data<>("Mediana", med));
                series.getData().add(new XYChart.Data<>("Q3", q3));
                series.getData().add(new XYChart.Data<>("Max", sorted.get(sorted.size() - 1)));

                if (sinVariacion) {
                    infoLabel.setText("Sin variación: todos los valores son iguales a " + sorted.get(0));
                }
            }
        }

        chart.getData().add(series);
        VBox root = new VBox(10, chart, infoLabel);
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    private static double percentile(List<Double> list, double p) {
        int index = (int) Math.ceil(p / 100.0 * list.size()) - 1;
        return list.get(Math.max(0, Math.min(index, list.size() - 1)));
    }
}
