package com.ml.spam.dictionary.models;

public class DatasetMetadata {
    private String id;
    private int instances;
    private int ham;
    private int spam;
    private String timestamp;

    public DatasetMetadata() {
        // Necesario para la deserialización con Jackson
    }

    public DatasetMetadata(String id, int instances, int ham, int spam, String timestamp) {
        this.id = id;
        this.instances = instances;
        this.ham = ham;
        this.spam = spam;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public int getInstances() {
        return instances;
    }

    public int getHam() {
        return ham;
    }

    public int getSpam() {
        return spam;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public void setHam(int ham) {
        this.ham = ham;
    }

    public void setSpam(int spam) {
        this.spam = spam;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
