package com.ml.spam.utils;

import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class JsonUtils {

    /**
     * Convierte un JSONArray en una lista de Strings.
     * @param jsonArray El JSONArray a convertir.
     * @return Una lista de Strings, o una lista vacía si el JSONArray es nulo.
     */
    public static List<String> jsonArrayToStringList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return Collections.emptyList();
        }
        return jsonArray.toList().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
    public static List<String> jsonArrayToStringList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        }
        return list;
    }


    public static void validateJsonKeys(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            if (!jsonObject.has(category.name().toLowerCase())) {
                throw new IllegalArgumentException("Falta la categoría: " + category.name().toLowerCase());
            }
        }
    }

    public static Map<WordCategory, List<String>> jsonToCategoryMap(JSONObject jsonObject) {
        Map<WordCategory, List<String>> categoryMap = new HashMap<>();

        for (WordCategory category : WordCategory.values()) {
            if (jsonObject.has(category.name().toLowerCase())) {
                List<String> words = jsonArrayToStringList(
                        jsonObject.optJSONArray(category.name().toLowerCase())
                );
                categoryMap.put(category, words);
            }
        }

        return categoryMap;
    }


    // Valida que el JSON tenga las categorías necesarias
    public static void validateJsonStructure(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            if (!jsonObject.has(category.name().toLowerCase())) {
                throw new IllegalArgumentException("Falta la categoría: " + category.name().toLowerCase());
            }
        }
    }

    public static Map<WordCategory, List<String>> jsonToCategoryMap(JSONObject jsonObject) {
        Map<WordCategory, List<String>> categoryMap = new HashMap<>();

        for (WordCategory category : WordCategory.values()) {
            if (jsonObject.has(category.name().toLowerCase())) {
                List<String> words = jsonArrayToStringList(
                        jsonObject.optJSONArray(category.name().toLowerCase())
                );
                categoryMap.put(category, words);
            }
        }

        return categoryMap;
    }
}
