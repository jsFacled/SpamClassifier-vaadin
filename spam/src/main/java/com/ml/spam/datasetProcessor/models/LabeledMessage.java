package com.ml.spam.datasetProcessor.models;

public class LabeledMessage {
    private String content;
    private String label;

    public LabeledMessage(String content, String label) {
        this.content = content;
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public String getLabel() {
        return label;
    }
}
