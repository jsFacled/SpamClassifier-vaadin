package com.ml.spam.dictionary.models;

import java.util.ArrayList;
import java.util.List;

public class SpamDictionaryMetadata {
    private int totalInstances = 0;
    private int totalHam = 0;
    private int totalSpam = 0;
    private int totalDatasetsProcessed = 0;
    private final List<DatasetMetadata> datasetDetails = new ArrayList<>();

    public void incrementHam() {
        totalHam++;
        totalInstances++;
    }

    public void incrementSpam() {
        totalSpam++;
        totalInstances++;
    }

    public void addDataset(DatasetMetadata dataset) {
        datasetDetails.add(dataset);
        totalDatasetsProcessed++;
    }

    public int getTotalInstances() {
        return totalInstances;
    }

    public int getTotalHam() {
        return totalHam;
    }

    public int getTotalSpam() {
        return totalSpam;
    }

    public int getTotalDatasetsProcessed() {
        return totalDatasetsProcessed;
    }

    public List<DatasetMetadata> getDatasetDetails() {
        return datasetDetails;
    }
}
