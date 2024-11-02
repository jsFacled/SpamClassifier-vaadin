package com.ml.spam.mlmodel;

import javax.visrec.ml.classification.Classifiable;
import java.util.HashMap;
import java.util.Map;

public final class Email implements Classifiable<float[], Boolean> {

    // Definimos una lista de 48 palabras clave comunes en correos de spam
    private static final String[] KEYWORDS = {
            "free", "offer", "win", "money", "cash", "prize", "click", "buy", "cheap", "credit",
            "urgent", "winner", "limited", "hurry", "exclusive", "deal", "save", "congratulations", "sale", "gift",
            "bonus", "discount", "bargain", "guarantee", "refund", "investment", "luxury", "profit", "earn",
            "trial", "satisfaction", "apply", "claim", "miracle", "promise", "risk-free", "membership", "instant",
            "insurance", "opportunity", "expiring", "important", "alert", "hot", "lowest", "act now", "donation"
    };

    private final float[] emailFeatures;
    private Boolean isSpam;

    // Constructor para predicción (sin etiqueta isSpam)
    public Email(String message) {
        this(message, null);
    }

    // Constructor para entrenamiento (con etiqueta isSpam)
    public Email(String message, Boolean isSpam) {
        this.isSpam = isSpam;
        emailFeatures = new float[57];  // Inicializamos el vector de características con 57 posiciones
        extractFeatures(message);       // Extraemos características del mensaje
    }

    private void extractFeatures(String message) {
        // 1. Extraer frecuencia de palabras clave
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : KEYWORDS) {
            wordCounts.put(word, countOccurrences(message, word));
        }

        // Llenar las primeras 48 posiciones con las frecuencias de las palabras clave
        for (int i = 0; i < KEYWORDS.length; i++) {
            emailFeatures[i] = wordCounts.getOrDefault(KEYWORDS[i], 0);
        }

        // 2. Caracteres especiales
        emailFeatures[48] = countCharacterOccurrences(message, '!');
        emailFeatures[49] = countCharacterOccurrences(message, '$');
        emailFeatures[50] = countCharacterOccurrences(message, '#');
        emailFeatures[51] = countCharacterOccurrences(message, '%');
        emailFeatures[52] = countCharacterOccurrences(message, '&');
        emailFeatures[53] = countCharacterOccurrences(message, '*');

        // 3. Métricas de mayúsculas
        emailFeatures[54] = calculateCapitalAverage(message);
        emailFeatures[55] = calculateCapitalLongest(message);
        emailFeatures[56] = calculateCapitalTotal(message);
    }

    private int countOccurrences(String text, String word) {
        return text.split("\\b" + word + "\\b", -1).length - 1;
    }

    private int countCharacterOccurrences(String text, char character) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == character) count++;
        }
        return count;
    }

    private float calculateCapitalAverage(String text) {
        int capitalCount = 0;
        int totalChars = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                totalChars++;
                if (Character.isUpperCase(c)) {
                    capitalCount++;
                }
            }
        }
        return totalChars > 0 ? (float) capitalCount / totalChars : 0;
    }

    private float calculateCapitalLongest(String text) {
        int maxLength = 0;
        int currentLength = 0;

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                currentLength++;
                maxLength = Math.max(maxLength, currentLength);
            } else {
                currentLength = 0;
            }
        }
        return maxLength;
    }

    private float calculateCapitalTotal(String text) {
        int capitalCount = 0;
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capitalCount++;
            }
        }
        return capitalCount;
    }

    @Override
    public float[] getClassifierInput() {
        return emailFeatures;
    }

    @Override
    public Boolean getTargetClass() {
        return isSpam;
    }
}
