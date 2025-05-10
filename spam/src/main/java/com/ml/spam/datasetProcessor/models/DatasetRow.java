package com.ml.spam.datasetProcessor.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatasetRow {

    private final Map<String, Double> features;

    public DatasetRow(Map<String, Double> features) {
        this.features = new LinkedHashMap<>(features); // mantiene el orden
    }

    public Map<String, Double> getFeatures() {
        return features;
    }

    public String toCsvRow() {
        StringBuilder sb = new StringBuilder();
        for (Double value : features.values()) {
            sb.append(value).append(",");
        }
        if (!features.isEmpty()) {
            sb.setLength(sb.length() - 1); // quitar última coma
        }
        return sb.toString();
    }
}
