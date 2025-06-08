package com.ml.spam.analysis;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import java.io.*;
import java.util.*;

public class DatasetInspectorUI extends Application {

    private String csvPath;
    private List<String> columnas = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Dataset Inspector App");

        Label archivoLabel = new Label("No hay archivo cargado");
        Button cargarBtn = new Button("Seleccionar CSV");
        Button resumenBtn = new Button("Mostrar resumen");
        Button histogramaBtn = new Button("Mostrar histograma");
        Button boxplotBtn = new Button("Mostrar boxplot");
        Button correlacionBtn = new Button("Calcular correlación");
        Button verColumnasBtn = new Button("Ver columnas");
        Button headBtn = new Button("Ver primeras filas");
        Button tiposBtn = new Button("Ver tipos de datos");
        Button describeBtn = new Button("Describir datos");
        Button uniqueBtn = new Button("Valores únicos");
        Button valueCountsBtn = new Button("Conteo por categoría");
        Button totalFreqBtn = new Button("Sumar todas freq_palabra");

        ComboBox<String> col1Combo = new ComboBox<>();
        ComboBox<String> col2Combo = new ComboBox<>();
        col1Combo.setPromptText("Seleccionar columna 1");
        col2Combo.setPromptText("Seleccionar columna 2");

        cargarBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo CSV");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                csvPath = file.getAbsolutePath();
                archivoLabel.setText("Archivo cargado: " + file.getName());
                columnas = leerCabecera(csvPath);
                col1Combo.getItems().setAll(columnas);
                col2Combo.getItems().setAll(columnas);
            }
        });

        resumenBtn.setOnAction(e -> {
            try {
                DatasetInspector.mostrarResumenGeneral(csvPath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        histogramaBtn.setOnAction(e -> {
            String col = col1Combo.getValue();
            if (col != null) {
                try {
                    DatasetInspector.mostrarHistograma(csvPath, col);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        boxplotBtn.setOnAction(e -> {
            String col = col1Combo.getValue();
            if (col != null) {
                try {
                    DatasetInspector.mostrarBoxplot(csvPath, col);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        correlacionBtn.setOnAction(e -> {
            String col1 = col1Combo.getValue();
            String col2 = col2Combo.getValue();
            if (col1 != null && col2 != null) {
                try {
                    DatasetInspector.calcularCorrelacionManual(csvPath, col1, col2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        uniqueBtn.setOnAction(e -> {
            String col = col1Combo.getValue();
            if (csvPath != null && col != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
                    String[] headers = reader.readLine().split(",");
                    int colIndex = Arrays.asList(headers).indexOf(col);
                    Set<String> valores = new LinkedHashSet<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",", -1);
                        if (colIndex < parts.length) valores.add(parts[colIndex]);
                    }
                    TextArea ta = new TextArea(String.join("\n", valores));
                    ta.setWrapText(false); ta.setEditable(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Valores únicos");
                    alert.setHeaderText("df[\"" + col + "\"].unique()");
                    alert.getDialogPane().setContent(ta);
                    alert.setResizable(true);
                    alert.getDialogPane().setPrefSize(400, 400);
                    alert.showAndWait();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        valueCountsBtn.setOnAction(e -> {
            String col = col1Combo.getValue();
            if (csvPath != null && col != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
                    String[] headers = reader.readLine().split(",");
                    int colIndex = Arrays.asList(headers).indexOf(col);
                    Map<String, Integer> conteo = new HashMap<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",", -1);
                        if (colIndex < parts.length) {
                            conteo.put(parts[colIndex], conteo.getOrDefault(parts[colIndex], 0) + 1);
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    conteo.entrySet().stream()
                            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                            .forEach(e2 -> sb.append(e2.getKey()).append(": ").append(e2.getValue()).append("\n"));
                    TextArea ta = new TextArea(sb.toString());
                    ta.setWrapText(false); ta.setEditable(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Conteo por categoría");
                    alert.setHeaderText("df[\"" + col + "\"].value_counts()");
                    alert.getDialogPane().setContent(ta);
                    alert.setResizable(true);
                    alert.getDialogPane().setPrefSize(400, 400);
                    alert.showAndWait();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        totalFreqBtn.setOnAction(e -> {
            try {
                DatasetInspector.showTotalFreqPerWord(csvPath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10,
                cargarBtn, archivoLabel,
                col1Combo, col2Combo,
                resumenBtn, histogramaBtn,
                boxplotBtn, correlacionBtn,
                verColumnasBtn, headBtn,
                tiposBtn, describeBtn,
                uniqueBtn, valueCountsBtn,
                totalFreqBtn
        );

        Scene scene = new Scene(root, 400, 750);
        stage.setScene(scene);
        stage.show();
    }

    private List<String> leerCabecera(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String header = reader.readLine();
            return Arrays.asList(header.split(","));
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void main(String[] args) {
        System.setProperty("prism.order", "sw");
        launch(args);
    }
}
