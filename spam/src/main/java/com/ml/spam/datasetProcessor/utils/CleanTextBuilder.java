package com.ml.spam.datasetProcessor.utils;


public class CleanTextBuilder {

    private String text;

    public CleanTextBuilder(String text) {
        this.text = text;
    }

    public static CleanTextBuilder from(String text) {
        return new CleanTextBuilder(text);
    }
//Quitar comillas
    public CleanTextBuilder removeSurroundingQuotes() {
        this.text = text.replaceAll("^\"{1,3}", "").replaceAll("\"{1,3}$", "");
        return this;
    }

    public CleanTextBuilder normalizeWhitespaces() {
        this.text = text.replaceAll("\\s+", " ").trim();
        return this;
    }

    public CleanTextBuilder toLowerCase() {
        this.text = text.toLowerCase();
        return this;
    }

    public CleanTextBuilder removeAccents() {
        this.text = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return this;
    }

    public String build() {
        return text;
    }
}
