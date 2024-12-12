package com.ml.spam.datasetProcessor.models;

public class InvalidRow {
    private String[] row;
    private String reason;

    public InvalidRow(String[] row, String reason) {
        this.row = row;
        this.reason = reason;
    }

    public String[] getRow() {
        return row;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "InvalidRow{row=" + String.join(",", row) + ", reason='" + reason + "'}";
    }
}
