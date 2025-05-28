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
    public static void launchResumenConTexto(Map<String, Integer> valores, String resumen) {
        launchResumen(valores); // Reutiliza el gráfico existente

        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(resumen);
            textArea.setWrapText(true);
            textArea.setEditable(false);
            textArea.setPrefSize(500, 200);

            javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(textArea);
            javafx.scene.Scene scene = new javafx.scene.Scene(vbox);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Resumen del Dataset");
            stage.setScene(scene);
            stage.show();
        });
    }

}
