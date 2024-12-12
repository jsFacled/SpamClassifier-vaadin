package com.ml.spam.datasetProcessor.models;

public class RowValidationResult {
    private boolean valid;
    private String message;
    private String label;

    public RowValidationResult(boolean valid, String message, String label) {
        this.valid = valid;
        this.message = message;
        this.label = label;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
