package com.ml.spam.datasetProcessor.models;

import java.util.List;
import java.util.Map;

public class ProcessedMessage {
    private List<String> tokens; // Lista de palabras tokenizadas
    private Map<String, Integer> wordFrequency; // Frecuencia de palabras en el mensaje
    private String label; // Etiqueta del mensaje (spam/ham)
    private int wordCount; // Número total de palabras en el mensaje
    private double rareSymbolProportion; // Proporción de símbolos raros
    private double stopWordFrequency; // Frecuencia de stopwords

    // Constructor principal
    public ProcessedMessage(List<String> tokens, Map<String, Integer> wordFrequency, String label, int wordCount, double rareSymbolProportion, double stopWordFrequency) {
        this.tokens = tokens;
        this.wordFrequency = wordFrequency;
        this.label = label;
        this.wordCount = wordCount;
        this.rareSymbolProportion = rareSymbolProportion;
        this.stopWordFrequency = stopWordFrequency;
    }

    // Getters y Setters
    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public Map<String, Integer> getWordFrequency() {
        return wordFrequency;
    }

    public void setWordFrequency(Map<String, Integer> wordFrequency) {
        this.wordFrequency = wordFrequency;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public double getRareSymbolProportion() {
        return rareSymbolProportion;
    }

    public void setRareSymbolProportion(double rareSymbolProportion) {
        this.rareSymbolProportion = rareSymbolProportion;
    }

    public double getStopWordFrequency() {
        return stopWordFrequency;
    }

    public void setStopWordFrequency(double stopWordFrequency) {
        this.stopWordFrequency = stopWordFrequency;
    }

    @Override
    public String toString() {
        return "ProcessedMessage{" +
                "tokens=" + tokens +
                ", wordFrequency=" + wordFrequency +
                ", label='" + label + '\'' +
                ", wordCount=" + wordCount +
                ", rareSymbolProportion=" + rareSymbolProportion +
                ", stopWordFrequency=" + stopWordFrequency +
                '}';
    }
}
