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

        ComboBox<String> col1Combo = new ComboBox<>();
        ComboBox<String> col2Combo = new ComboBox<>();
        col1Combo.setPromptText("Seleccionar columna 1");
        col2Combo.setPromptText("Seleccionar columna 2");

        Button resumenBtn = new Button("Mostrar resumen");
        Button histogramaBtn = new Button("Mostrar histograma");
        Button boxplotBtn = new Button("Mostrar boxplot");
        Button correlacionBtn = new Button("Calcular correlación");
        Button verColumnasBtn = new Button("Ver columnas");
        Button headBtn = new Button("Ver primeras filas");
        Button tiposBtn = new Button("Ver tipos de datos");
        Button describeBtn = new Button("Describir datos");

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

        verColumnasBtn.setOnAction(e -> {
            try {
                if (csvPath != null) {
                    BufferedReader reader = new BufferedReader(new FileReader(csvPath));
                    String[] headers = reader.readLine().split(",");

                    StringBuilder sbH = new StringBuilder();
                    for (String col : headers) {
                        sbH.append(col).append("\t");
                    }
                    sbH.append("\n\nTotal: ").append(headers.length);

                    StringBuilder sbV = new StringBuilder();
                    for (String col : headers) {
                        sbV.append("- ").append(col).append("\n");
                    }
                    sbV.append("\nTotal: ").append(headers.length);

                    TabPane tabPane = new TabPane();
                    TextArea taH = new TextArea(sbH.toString());
                    taH.setWrapText(true); taH.setEditable(false);
                    TextArea taV = new TextArea(sbV.toString());
                    taV.setWrapText(true); taV.setEditable(false);
                    Tab tab1 = new Tab("Horizontal", taH);
                    Tab tab2 = new Tab("Vertical", taV);
                    tab1.setClosable(false); tab2.setClosable(false);
                    tabPane.getTabs().addAll(tab1, tab2);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Columnas (" + headers.length + ")");
                    alert.setHeaderText("Vista de columnas");
                    alert.getDialogPane().setContent(tabPane);
                    alert.setResizable(true);
                    alert.getDialogPane().setPrefSize(700, 500);
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        headBtn.setOnAction(e -> {
            try {
                if (csvPath != null) {
                    BufferedReader reader = new BufferedReader(new FileReader(csvPath));
                    String header = reader.readLine();
                    StringBuilder sb = new StringBuilder();
                    sb.append(header).append("\n");
                    for (int i = 0; i < 10; i++) {
                        String line = reader.readLine();
                        if (line == null) break;
                        sb.append(line).append("\n");
                    }
                    TextArea ta = new TextArea(sb.toString());
                    ta.setWrapText(false);
                    ta.setEditable(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Primeras filas");
                    alert.setHeaderText("df.head(10)");
                    alert.getDialogPane().setContent(ta);
                    alert.setResizable(true);
                    alert.getDialogPane().setPrefSize(700, 400);
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        tiposBtn.setOnAction(e -> {
            try {
                if (csvPath != null) {
                    BufferedReader reader = new BufferedReader(new FileReader(csvPath));
                    String[] headers = reader.readLine().split(",");
                    String[] sample = reader.readLine().split(",", -1);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < headers.length; i++) {
                        String tipo = "string";
                        try { Double.parseDouble(sample[i]); tipo = "float"; } catch (Exception ignored) {}
                        sb.append(headers[i]).append(" -> ").append(tipo).append("\n");
                    }
                    TextArea ta = new TextArea(sb.toString());
                    ta.setWrapText(false); ta.setEditable(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Tipos de datos");
                    alert.setHeaderText("df.dtypes (estimado)");
                    alert.getDialogPane().setContent(ta);
                    alert.setResizable(true);
                    alert.getDialogPane().setPrefSize(600, 400);
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        describeBtn.setOnAction(e -> {
            try {
                if (csvPath != null) {
                    BufferedReader reader = new BufferedReader(new FileReader(csvPath));
                    String[] headers = reader.readLine().split(",");
                    int N = headers.length;
                    List<List<Double>> columnas = new ArrayList<>(Collections.nCopies(N, null));
                    for (int i = 0; i < N; i++) columnas.set(i, new ArrayList<>());
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",", -1);
                        for (int i = 0; i < Math.min(parts.length, N); i++) {
                            try { columnas.get(i).add(Double.parseDouble(parts[i])); } catch (Exception ignored) {}
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < N; i++) {
                        List<Double> vals = columnas.get(i);
                        if (vals.isEmpty()) continue;
                        double min = Collections.min(vals);
                        double max = Collections.max(vals);
                        double avg = vals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                        double std = Math.sqrt(vals.stream().mapToDouble(d -> (d - avg)*(d - avg)).sum() / vals.size());
                        sb.append(headers[i]).append(": min=").append(min).append(" max=").append(max)
                                .append(" mean=").append(avg).append(" std=").append(std).append("\n");
                    }
                    TextArea ta = new TextArea(sb.toString());
                    ta.setWrapText(false); ta.setEditable(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Resumen numérico");
                    alert.setHeaderText("df.describe()");
                    alert.getDialogPane().setContent(ta);
                    alert.setResizable(true);
                    alert.getDialogPane().setPrefSize(700, 400);
                    alert.showAndWait();
                }
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
                tiposBtn, describeBtn
        );
        Scene scene = new Scene(root, 400, 650);
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
