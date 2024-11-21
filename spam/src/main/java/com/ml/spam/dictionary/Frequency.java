package com.ml.spam.dictionary;


/*
Modelo simplificado para almacenar las frecuencias de spam y ham asociadas a cada palabra o símbolo.
Clase simple para reemplazar WordData, eliminando redundancia (ya no contiene la palabra como atributo).
 */
public class Frequency {
    private int spamFrequency;
    private int hamFrequency;

    public Frequency(int spamFrequency, int hamFrequency) {
        this.spamFrequency = spamFrequency;
        this.hamFrequency = hamFrequency;
    }

    public int getSpamFrequency() {
        return spamFrequency;
    }

    public void incrementSpamFrequency() {
        this.spamFrequency++;
    }

    public int getHamFrequency() {
        return hamFrequency;
    }

    public void incrementHamFrequency() {
        this.hamFrequency++;
    }

    @Override
    public String toString() {
        return "{spamFrequency=" + spamFrequency + ", hamFrequency=" + hamFrequency + "}";
    }
}
