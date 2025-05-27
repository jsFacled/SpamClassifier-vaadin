package com.ml.spam.analysis;

import java.util.List;
import java.util.Map;

public class ChartLauncher {
    public static void launchResumen(Map<String, Integer> valores) {
        UnifiedChartApp.mostrar("resumen", "Resumen", null, valores);
    }

    public static void launchHistogram(String columnName, List<Double> values) {
        UnifiedChartApp.mostrar("histogram", columnName, values, null);
    }

    public static void launchBoxplot(String columnName, List<Double> values) {
        UnifiedChartApp.mostrar("boxplot", columnName, values, null);
    }
}
