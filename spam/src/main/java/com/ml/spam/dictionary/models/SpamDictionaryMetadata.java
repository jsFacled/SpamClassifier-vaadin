package com.ml.spam.dictionary.models;

import java.util.ArrayList;
import java.util.List;

public class SpamDictionaryMetadata {
    private String exportedDictionaryFileName;
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


    public String getExportedDictionaryFileName() {
        return exportedDictionaryFileName;
    }

    public void setExportedDictionaryFileName(String fileName) {
        this.exportedDictionaryFileName = fileName;
    }
    public void incrementDatasetsProcessed() {
        totalDatasetsProcessed++;
    }
    public void setTotalSpam(int totalSpam) {
        this.totalSpam = totalSpam;
    }

    public void setTotalHam(int totalHam) {
        this.totalHam = totalHam;
    }

    public void setTotalInstances(int totalInstances) {
        this.totalInstances = totalInstances;
    }

    public void setTotalDatasetsProcessed(int totalDatasetsProcessed) {
        this.totalDatasetsProcessed = totalDatasetsProcessed;
    }

    public void setDatasetDetails(List<DatasetMetadata> datasetDetails) {
        this.datasetDetails.clear();
        if (datasetDetails != null) {
            this.datasetDetails.addAll(datasetDetails);
        }
    }

}
