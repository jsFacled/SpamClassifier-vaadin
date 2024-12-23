package com.ml.spam.dictionary.models;

import org.json.JSONObject;

import java.util.*;

public class LexemeDictionary {

    private final Map<LexCategory, Set<String>> textLexemes;
    private final Map<NumCategory, Set<String>> numLexemes;
    private final Map<String, Set<String>> contextualLexemes;

    public LexemeDictionary() {
        contextualLexemes = new HashMap<>();
        textLexemes = new HashMap<>();
        numLexemes = new HashMap<>();
    }

    // Inicializa los lexemas desde un JSON objet

    public void loadLexemesFromJson2(JSONObject json) {
        for (LexCategory category : LexCategory.values()) {
            Set<String> words = new HashSet<>(jsonArrayToSet(json.optJSONArray(category.getName())));
            textLexemes.put(category, words);
        }

        for (NumCategory category : NumCategory.values()) {
            Set<String> words = new HashSet<>(jsonArrayToSet(json.optJSONArray(category.toString())));
            numLexemes.put(category, words);
        }
        for (NumCategory category : NumCategory.values()) {
            Set<String> words = new HashSet<>(jsonArrayToSet(json.optJSONArray(category.toString())));
            numLexemes.put(category, words);
        }

    }
    public void loadLexemesFromJson(JSONObject json) {
        for (LexCategory category : LexCategory.values()) {
            Set<String> words = new HashSet<>(jsonArrayToSet(json.optJSONArray(category.getName())));
            textLexemes.put(category, words);
        }

        for (NumCategory category : NumCategory.values()) {
            Set<String> words = new HashSet<>(jsonArrayToSet(json.optJSONArray(category.toString())));
            numLexemes.put(category, words);
        }
        for (NumCategory category : NumCategory.values()) {
            Set<String> words = new HashSet<>(jsonArrayToSet(json.optJSONArray(category.toString())));
            numLexemes.put(category, words);
        }

    }
    // Busca en categorías de lexemas

    public String findInLexCategories(String token) {
        for (Map.Entry<LexCategory, Set<String>> entry : textLexemes.entrySet()) {
            if (entry.getValue().contains(token)) {
                return entry.getKey().getName();
            }
        }
        return null;
    }
    // Busca en categorías numéricas

    public String findInNumCategories(String token) {
        for (Map.Entry<NumCategory, Set<String>> entry : numLexemes.entrySet()) {
            if (entry.getValue().contains(token)) {
                return entry.getKey().toString();
            }
        }
        return null;
    }
    // Método auxiliar para convertir JSONArray a Set

    private Set<String> jsonArrayToSet(org.json.JSONArray jsonArray) {
        Set<String> result = new HashSet<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                result.add(jsonArray.getString(i));
            }
        }
        return result;
    }
    // Métodos adicionales para normalizaciones si son necesarios

    public String normalizeToken(String token) {
        // Normalizar un token eliminando tildes o aplicando otras reglas
        return token.toLowerCase();
    }
    // Obtener todos los lexemas para depuración o análisis

    public Map<LexCategory, Set<String>> getTextLexemes() {
        return Collections.unmodifiableMap(textLexemes);
    }
    public Map<NumCategory, Set<String>> getNumLexemes() {
        return Collections.unmodifiableMap(numLexemes);
    }
    public Map<String, Set<String>> getContextualLexemes() {
        return contextualLexemes;
    }



}
