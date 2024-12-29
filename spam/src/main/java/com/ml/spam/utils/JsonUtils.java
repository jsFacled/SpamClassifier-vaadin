package com.ml.spam.utils;

import com.ml.spam.dictionary.models.FrequencyKey;
import com.ml.spam.dictionary.models.LexemeRepositoryCategories;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class JsonUtils {

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
            if (!jsonObject.has(category.getJsonKey())) {
                throw new IllegalArgumentException("Falta la categoría: " + category.getJsonKey());
            }
            System.out.println("[DEBUGG] Categoría en CategorizedWords validadas en json: "+category);
        }
    }


    //Valida que las frecuencias estén en cero
    public static void validateJsonFrequenciesZero(JSONObject jsonObject) {
        for (WordCategory category : WordCategory.values()) {
            JSONObject categoryJson = jsonObject.optJSONObject(category.name().toLowerCase());
            if (categoryJson != null) {
                for (String word : categoryJson.keySet()) {
                    JSONObject frequencies = categoryJson.getJSONObject(word);

                    for (FrequencyKey key : FrequencyKey.values()) {
                        int frequency = frequencies.optInt(key.getKey(), -1);
                        if (frequency != 0) {
                            throw new IllegalArgumentException("Frecuencia no válida para '" + word + "' en la categoría '" + category.name() + "': " + key.getKey() + "=" + frequency);
                        }
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

        System.out.println("Claves en el JSON: " + jsonObject.keySet()); // Depuración

        for (WordCategory category : WordCategory.values()) {
            String jsonKey = category.getJsonKey(); // Usar jsonKey directamente
            System.out.println("Procesando categoría: " + jsonKey); // Depuración

            if (jsonObject.has(jsonKey)) {
                JSONArray jsonArray = jsonObject.optJSONArray(jsonKey);

                if (jsonArray != null) {
                    List<String> words = jsonArrayToStringList(jsonArray);
                    System.out.println("Palabras encontradas para " + jsonKey + ": " + words); // Depuración
                    categoryMap.put(category, words);
                } else {
                    System.out.println("No hay palabras en la categoría: " + jsonKey); // Depuración
                }
            } else {
                System.out.println("Clave no encontrada en JSON: " + jsonKey); // Depuración
            }
        }

        System.out.println("Mapa generado: " + categoryMap); // Depuración final
        return categoryMap;
    }

    /**
     * Convierte un diccionario categorizado en un objeto JSON.
     * @return Un objeto JSON que representa el diccionario completo.
     */
    public static JSONObject categorizedWordsToJson(Map<WordCategory, Map<String, WordData>> categorizedWordsMap) {
        JSONObject json = new JSONObject();

        categorizedWordsMap.forEach((category, wordsMap) -> {
            JSONObject categoryJson = new JSONObject();
            wordsMap.forEach((word, wordData) -> {
                JSONObject wordDataJson = new JSONObject();

                // Usar el enum para las claves
                wordDataJson.put(FrequencyKey.SPAM_FREQUENCY.getKey(), wordData.getSpamFrequency());
                wordDataJson.put(FrequencyKey.HAM_FREQUENCY.getKey(), wordData.getHamFrequency());

                categoryJson.put(word, wordDataJson);
            });

            // Agregar la categoría al JSON principal
            json.put(category.name().toLowerCase(), categoryJson);
        });

        return json;
    }


    /**
     * // LEXEMES
     */
    public static void validateLexemeJsonStructure(JSONObject jsonObject) {
        for (LexemeRepositoryCategories category : LexemeRepositoryCategories.values()) {
            if (!jsonObject.has(category.getJsonKey())) {
                throw new IllegalArgumentException(
                        "Validación fallida: Falta la categoría principal '" + category.getJsonKey() + "' en el JSON proporcionado."
                );
            }
        }
        System.out.println("[INFO] Todas las categorías principales están presentes en el JSON.");
    }

    public static Map<LexemeRepositoryCategories, Set<String>> jsonToLexemeMap(JSONObject jsonObject) {
        Map<LexemeRepositoryCategories, Set<String>> lexemesMap = new HashMap<>();

        for (LexemeRepositoryCategories category : LexemeRepositoryCategories.values()) {
            JSONObject subCategories = jsonObject.optJSONObject(category.getJsonKey());
            if (subCategories != null) {
                Set<String> allLexemes = new HashSet<>();

                for (String subCategory : subCategories.keySet()) {
                    JSONArray lexemeArray = subCategories.optJSONArray(subCategory);
                    if (lexemeArray != null) {
                        allLexemes.addAll(jsonArrayToStringList(lexemeArray));
                    }
                }

                lexemesMap.put(category, allLexemes);
            } else {
                lexemesMap.put(category, Collections.emptySet());
            }
        }

        return lexemesMap;
    }

}
