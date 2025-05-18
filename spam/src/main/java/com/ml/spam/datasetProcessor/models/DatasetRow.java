package com.ml.spam.datasetProcessor.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record DatasetRow(Map<String, Double> features) {

    public List<Double> getValuesOrdered(List<String> columnOrder) {
        List<Double> orderedValues = new ArrayList<>(columnOrder.size());
        for (String column : columnOrder) {
            Double value = features.getOrDefault(column, 0.0);
            orderedValues.add(value);
        }
        return orderedValues;
    }
}
