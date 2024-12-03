package com.ml.spam.utils;

import com.ml.spam.dictionary.models.WordCategory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class JsonUtils {

    /**
     * Convierte un JSONArray en una lista de Strings.
     * @param jsonArray El JSONArray a convertir.
     * @return Una lista de Strings, o una lista vacía si el JSONArray es nulo.
     */
    public static List<String> jsonArrayToStringList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        }
        return list;
    }

    /**
     * Valida que el JSONObject contenga todas las claves necesarias basadas en las categorías de WordCategory.
     * @param jsonObject El JSON a validar.
     * @throws IllegalArgumentException Si falta alguna clave.
     */
    public static void validateJsonStructure(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            if (!jsonObject.has(category.name().toLowerCase())) {
                throw new IllegalArgumentException("Falta la categoría: " + category.name().toLowerCase());
            }
        }
    }

    /**
     * Transforma un JSONObject en un Map<WordCategory, List<String>>.
     * Cada clave del JSON se mapea a una categoría, y su valor es una lista de palabras.
     * @param jsonObject El JSON a transformar.
     * @return Un Map con WordCategory como clave y una lista de palabras como valor.
     */
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
