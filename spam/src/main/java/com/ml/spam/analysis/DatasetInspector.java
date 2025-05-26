package com.ml.spam.analysis;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.Plot;

import java.io.IOException;

public class DatasetInspector {

    // Carga y muestra resumen del CSV
    public static void mostrarResumenGeneral(String pathCsv) throws IOException {
        Table data = Table.read().csv(pathCsv);
        System.out.println(data.structure());
        System.out.println(data.summary());
    }

    // Muestra la distribución de clases (label)
    public static void verDistribucionClases(Table data) {
        System.out.println(data.categoricalColumn("label").valueCounts());
    }

    // Histograma de una feature numérica
    public static void mostrarHistograma(Table data, String columnName) {
        NumberColumn<?> col = data.numberColumn(columnName);
        Figure fig = Histogram.create(columnName, col);
        Plot.show(fig);
    }

    // Boxplot de una feature para ver outliers
    public static void mostrarBoxplot(Table data, String columnName) {
        NumberColumn<?> col = data.numberColumn(columnName);
        Figure fig = BoxPlot.create(columnName, col);
        Plot.show(fig);
    }
}
