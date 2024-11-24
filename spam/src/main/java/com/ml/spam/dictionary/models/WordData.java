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

    public String getWord() {
        return word;
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
