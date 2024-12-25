package com.ml.spam.dictionary.models;


public class WordData {
    private final String word;
    private int spamFrequency;
    private int hamFrequency;

    public WordData(String word) {
        this.word = word;
        this.spamFrequency = 0;
        this.hamFrequency = 0;
    }

    public WordData(String word, int spamFrequency, int hamFrequency) {
        this.word = word;
        this.spamFrequency = spamFrequency;
        this.hamFrequency = hamFrequency;
    }

    public WordData(String word, String label) {
        this.word = word;
        if ("spam".equalsIgnoreCase(label)) {
            this.spamFrequency = 1;
            this.hamFrequency = 0;
        } else if ("ham".equalsIgnoreCase(label)) {
            this.spamFrequency = 0;
            this.hamFrequency = 1;
        } else {
            this.spamFrequency = 0;
            this.hamFrequency = 0;
        }
    }

    public String getWord() {
        return word;
    }

    public int getSpamFrequency() {
        return spamFrequency;
    }
    public void incrementSpamFrequency(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("La frecuencia no puede ser negativa.");
        }
        this.spamFrequency += count;
    }
    public int getHamFrequency() {
        return hamFrequency;
    }
    public void incrementHamFrequency(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("La frecuencia no puede ser negativa.");
        }
        this.hamFrequency += count;
    }

    public void resetFrequencies() {
        this.spamFrequency = 0;
        this.hamFrequency = 0;
    }

    @Override
    public String toString() {
        return "{word='" + word + "', spamFrequency=" + spamFrequency + ", hamFrequency=" + hamFrequency + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WordData wordData = (WordData) obj;
        return word.equals(wordData.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }
}
