package com.ml.spam.datasetProcessor.models;

public class LabeledMessage {
    private String message;
    private String label;

    public LabeledMessage(String content, String label) {
        this.message = content;
        this.label = label;
    }

    public String getMessage() {
        return message;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "LabeledMessage{" +
                "message='" + message + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
