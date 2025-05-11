package com.ml.spam.datasetProcessor.schema;

import com.ml.spam.datasetProcessor.models.DatasetColumnName;

import java.util.*;

public class DatasetSchema {

    private final List<String> strongSpamWordsOrdered;
    private final List<String> columnNamesOrdered;
    private final int columnCount;

    public DatasetSchema(Set<String> strongSpamWords) {
        // Ordenar las palabras clave
        this.strongSpamWordsOrdered = new ArrayList<>(strongSpamWords);
        Collections.sort(this.strongSpamWordsOrdered);

        // Construir header completo
        this.columnNamesOrdered = buildFullColumnHeader(strongSpamWordsOrdered);
        this.columnCount = columnNamesOrdered.size();
    }

    private List<String> buildFullColumnHeader(List<String> strongWords) {
        List<String> header = new ArrayList<>();

        for (String word : strongWords) {
            header.add(DatasetColumnName.FREQ.get() + word);
            header.add(DatasetColumnName.RELATIVE_FREQ_NORM.get() + word);
            header.add(DatasetColumnName.WEIGHT.get() + word);
            header.add(DatasetColumnName.POLARITY.get() + word);
        }

        for (DatasetColumnName col : DatasetColumnName.values()) {
            if (!isPerWordFeature(col)) {
                header.add(col.get());
            }
        }

        return header;
    }

    private boolean isPerWordFeature(DatasetColumnName col) {
        return col == DatasetColumnName.FREQ
                || col == DatasetColumnName.RELATIVE_FREQ_NORM
                || col == DatasetColumnName.WEIGHT
                || col == DatasetColumnName.POLARITY;
    }

    public List<String> getColumnNames() {
        return columnNamesOrdered;
    }

    public List<String> getStrongSpamWords() {
        return strongSpamWordsOrdered;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public boolean isValidColumn(String name) {
        return columnNamesOrdered.contains(name);
    }

    public int getIndexOf(String columnName) {
        return columnNamesOrdered.indexOf(columnName);
    }

    public void validateRow(Map<String, Double> row) {
        if (row.size() != columnCount) {
            throw new IllegalArgumentException("Fila inválida: cantidad de columnas incorrecta (" + row.size() + " vs " + columnCount + ")");
        }
        for (String expected : columnNamesOrdered) {
            if (!row.containsKey(expected)) {
                throw new IllegalArgumentException("Fila inválida: falta la columna '" + expected + "'");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[DATASET SCHEMA]").append("\n");
        sb.append("- Palabras strongSpamWord: ").append(strongSpamWordsOrdered.size()).append("\n");
        sb.append("- Columnas totales: ").append(columnCount).append("\n");
        sb.append("- Primeras columnas:\n");

        for (int i = 0; i < Math.min(8, columnNamesOrdered.size()); i++) {
            sb.append("  ").append(i).append(": ").append(columnNamesOrdered.get(i)).append("\n");
        }

        sb.append("- Últimas columnas:\n");
        for (int i = Math.max(0, columnCount - 5); i < columnCount; i++) {
            sb.append("  ").append(i).append(": ").append(columnNamesOrdered.get(i)).append("\n");
        }

        return sb.toString();
    }

}
