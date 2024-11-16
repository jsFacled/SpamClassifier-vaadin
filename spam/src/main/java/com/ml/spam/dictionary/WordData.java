package com.ml.spam.dictionary;
public class WordData {
    private final String word;
    private int spamFrequency;
    private int hamFrequency;

    // Constructor
    public WordData(String word, int spamFrequency, int hamFrequency) {
        this.word = word;
        this.spamFrequency = spamFrequency;
        this.hamFrequency = hamFrequency;
    }

    // Incrementa la frecuencia en mensajes spam
    public void incrementSpamFrequency() {
        this.spamFrequency++;
    }

    // Incrementa la frecuencia en mensajes ham
    public void incrementHamFrequency() {
        this.hamFrequency++;
    }

    // Getters
    public String getWord() {
        return word;
    }

    public int getSpamFrequency() {
        return spamFrequency;
    }

    public int getHamFrequency() {
        return hamFrequency;
    }

    @Override
    public String toString() {
        return "WordData{" +
                "word='" + word + '\'' +
                ", spamFrequency=" + spamFrequency +
                ", hamFrequency=" + hamFrequency +
                '}';
    }
}
