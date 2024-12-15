package com.ml.spam.utils;

import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class JsonUtils {


        // Otros métodos...

        /**
         * Normaliza las claves y valores de un JSONObject eliminando acentos.
         * @param jsonObject El objeto JSON a normalizar.
         * @return Un nuevo JSONObject con las claves y valores normalizados.
         */



        public static JSONObject normalizeJson(JSONObject jsonObject) {
            JSONObject normalizedJson = new JSONObject();
            for (String key : jsonObject.keySet()) {
                // Normaliza la clave
                String normalizedKey = normalizeString(key);
                // Obtiene el valor
                Object value = jsonObject.get(key);

                if (value instanceof JSONObject) {
                    // Si el valor es un JSONObject, normalízalo recursivamente
                    normalizedJson.put(normalizedKey, normalizeJson((JSONObject) value));
                } else if (value instanceof String) {
                    // Si el valor es una cadena, normalízalo
                    normalizedJson.put(normalizedKey, normalizeString((String) value));
                } else {
                    // De lo contrario, simplemente inserta el valor
                    normalizedJson.put(normalizedKey, value);
                }
            }
            return normalizedJson;
        }


    // Normaliza una cadena eliminando acentos y caracteres no ASCII
    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }
        // Normaliza a forma de composición canónica (NFD)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Elimina los caracteres no ASCII (como diacríticos)
        return Pattern.compile("\\p{M}").matcher(normalized).replaceAll("");
    }


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
    public static void validateWordCategoryJsonStructure(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            if (!jsonObject.has(category.name().toLowerCase())) {
                throw new IllegalArgumentException("Falta la categoría: " + category.name().toLowerCase());
            }
        }
    }

    //Valida que las frecuencias estén en cero
    public static void validateJsonFrequenciesZero(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            JSONObject categoryJson = jsonObject.optJSONObject(category.name().toLowerCase());
            if (categoryJson != null) {
                for (String word : categoryJson.keySet()) {
                    JSONObject frequencies = categoryJson.getJSONObject(word);
                    int spamFrequency = frequencies.optInt("spamFrequency", -1);
                    int hamFrequency = frequencies.optInt("hamFrequency", -1);

                    if (spamFrequency != 0 || hamFrequency != 0) {
                        throw new IllegalArgumentException("Frecuencias no válidas para la palabra '" + word + "' en la categoría '" + category.name() + "'");
                    }
                }
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

    /**
     * Convierte un diccionario categorizado en un objeto JSON.
     * @param categorizedDictionary Mapa de categorías y sus palabras con datos.
     * @return Un objeto JSON que representa el diccionario completo.
     */
    public static JSONObject categorizedWordsToJson(Map<WordCategory, Map<String, WordData>> categorizedDictionary) {
        JSONObject jsonObject = new JSONObject();
        for (WordCategory category : categorizedDictionary.keySet()) {
            jsonObject.put(category.name().toLowerCase(), categoryToJson(categorizedDictionary.get(category)));
        }
        return jsonObject;
    }

    /**
     * Convierte un mapa de WordData a un objeto JSON.
     * @param category Mapa de palabras con sus datos (frecuencias).
     * @return Un objeto JSON que representa una categoría.
     */
    private static JSONObject categoryToJson(Map<String, WordData> category) {
        JSONObject jsonCategory = new JSONObject();
        category.forEach((word, wordData) -> {
            JSONObject freqData = new JSONObject();
            freqData.put("spamFrequency", wordData.getSpamFrequency());
            freqData.put("hamFrequency", wordData.getHamFrequency());
            jsonCategory.put(word, freqData);
        });
        return jsonCategory;
    }
}
