package com.ml.spam.utils;

import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.handlers.ResourcesHandler;
import org.json.JSONObject;

public class JsonDebugHelper {


    public static void debugJsonLoad(String filePath, ResourcesHandler resourcesHandler) {
        try {
            // Paso 1: Cargar y mostrar el JSON raw
            JSONObject jsonObject = resourcesHandler.loadJson(filePath);
            System.out.println("\n=== JSON Raw Content ===");
            System.out.println(jsonObject.toString(2));

            // Paso 2: Verificar estructura de categorías
            System.out.println("\n=== Category Structure ===");
            for (WordCategory category : WordCategory.values()) {
                String categoryKey = category.name().toLowerCase();
                JSONObject categoryJson = jsonObject.optJSONObject(categoryKey);
                System.out.println("\nCategory: " + categoryKey);
                if (categoryJson != null) {
                    System.out.println("Word Count: " + categoryJson.length());
                    // Mostrar primeras 3 palabras como muestra
                    int count = 0;
                    for (String word : categoryJson.keySet()) {
                        if (count++ < 3) {
                            Object wordData = categoryJson.get(word);
                            System.out.println("Sample Word[" + count + "]: " + word);
                            System.out.println("Data: " + wordData.toString());
                        }
                    }
                } else {
                    System.out.println("WARNING: Category JSON is null");
                }
            }

            // Paso 3: Verificar estructura de frecuencias
            System.out.println("\n=== Frequency Structure ===");
            for (WordCategory category : WordCategory.values()) {
                String categoryKey = category.name().toLowerCase();
                JSONObject categoryJson = jsonObject.optJSONObject(categoryKey);
                if (categoryJson != null) {
                    for (String word : categoryJson.keySet()) {
                        Object wordData = categoryJson.get(word);
                        if (!(wordData instanceof JSONObject)) {
                            System.out.println("ERROR: Invalid word data structure for " + word);
                            System.out.println("Expected JSONObject, got: " + wordData.getClass().getName());
                            continue;
                        }
                        JSONObject frequencies = (JSONObject) wordData;
                        if (!frequencies.has("spamFrequency") || !frequencies.has("hamFrequency")) {
                            System.out.println("ERROR: Missing frequency data for " + word);
                            System.out.println("Available keys: " + frequencies.keySet());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during JSON debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}