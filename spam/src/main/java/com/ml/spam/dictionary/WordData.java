package com.ml.spam.dictionary;

/**
 * Rol:
 *      Modelo que representa una palabra con sus frecuencias en spam y ham.
 * Responsabilidades:
 *      Almacenar la palabra y sus frecuencias.
 *      Incrementar las frecuencias.
 *      Proveer acceso a los valores mediante getters.
 */

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

    //calcula el porcentaje de frecuencia relativa de spam frente a ham,
    public double getSpamProbability() {
        int total = spamFrequency + hamFrequency;
        return total == 0 ? 0 : (double) spamFrequency / total;
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


