package com.ml.spam.utils;

import com.ml.spam.dictionary.models.FrequencyKey;
import com.ml.spam.dictionary.models.CharSize;
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

            if (jsonObject.has(jsonKey)) {
                JSONArray jsonArray = jsonObject.optJSONArray(jsonKey);

                if (jsonArray != null) {
                    List<String> words = jsonArrayToStringList(jsonArray);
                    categoryMap.put(category, words);
                } else {
                    System.out.println("No hay palabras en la categoría: " + jsonKey); // Depuración
                }
            } else {
                System.out.println("Clave no encontrada en JSON: " + jsonKey); // Depuración
            }
        }
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

            // Usar el jsonKey en formato camelCase para la clave de la categoría
            json.put(category.getJsonKey(), categoryJson);
        });

        return json;
    }


    /**
     * // LEXEMES
     */
    public static void validateLexemeJsonStructure(JSONObject jsonObject) {
        for (CharSize category : CharSize.values()) {
            if (!jsonObject.has(category.getJsonKey())) {
                throw new IllegalArgumentException(
                        "Validación fallida: Falta la categoría principal '" + category.getJsonKey() + "' en el JSON proporcionado."
                );
            }
        }
        System.out.println("[INFO] Todas las categorías principales están presentes en el JSON.");
    }

    public static Map<CharSize, Set<String>> jsonToLexemeMap(JSONObject jsonObject) {
        // Mapa donde se almacenarán los lexemas clasificados por categoría
        Map<CharSize, Set<String>> lexemesMap = new HashMap<>();

        // Iterar sobre todas las categorías del enum CharSize
        for (CharSize category : CharSize.values()) {
            System.out.println("Procesando categoría: " + category.getJsonKey());

            // Obtener el objeto JSON asociado a la categoría
            JSONObject subCategories = jsonObject.optJSONObject(category.getJsonKey());
            if (subCategories != null) {
                Set<String> allLexemes = new HashSet<>();

                // Iterar sobre las claves de las subcategorías
                for (String subCategory : subCategories.keySet()) {
                    System.out.println("  Subcategoría encontrada: " + subCategory);

                    // Obtener el array de lexemas de la subcategoría
                    JSONArray lexemeArray = subCategories.optJSONArray(subCategory);
                    if (lexemeArray != null) {
                        // Convertir el array a una lista de strings y agregar al conjunto de lexemas
                        allLexemes.addAll(jsonArrayToStringList(lexemeArray));
                    } else {
                        System.out.println("  No se encontró array en la subcategoría: " + subCategory);
                    }
                }

                // Agregar la categoría y sus lexemas al mapa
                lexemesMap.put(category, allLexemes);
            } else {
                System.out.println("Categoría no encontrada en el JSON: " + category.getJsonKey());
                // Si no hay subcategorías, se agrega un conjunto vacío
                lexemesMap.put(category, Collections.emptySet());
            }
        }

        // Retornar el mapa con las categorías y sus lexemas
        return lexemesMap;
    }
    public static Map<CharSize, Map<String, Set<String>>> jsonToStructuredLexemeMap(JSONObject jsonObject) {
        // Mapa donde se almacenarán los lexemas organizados por categoría y subcategoría
        Map<CharSize, Map<String, Set<String>>> lexemesMap = new HashMap<>();

        // Iterar sobre todas las categorías del enum CharSize
        for (CharSize category : CharSize.values()) {
            System.out.println("Procesando categoría: " + category.getJsonKey());

            // Obtener el objeto JSON asociado a la categoría
            JSONObject subCategories = jsonObject.optJSONObject(category.getJsonKey());
            if (subCategories != null) {
                Map<String, Set<String>> subCategoryMap = new HashMap<>();

                // Iterar sobre las claves de las subcategorías
                for (String subCategory : subCategories.keySet()) {
                   // System.out.println("  Subcategoría encontrada: " + subCategory);

                    // Obtener el array de lexemas de la subcategoría
                    JSONArray lexemeArray = subCategories.optJSONArray(subCategory);
                    if (lexemeArray != null) {
                        // Convertir el array a un conjunto de strings
                        Set<String> lexemes = new HashSet<>(jsonArrayToStringList(lexemeArray));
                        subCategoryMap.put(subCategory, lexemes);
                    } else {
                        System.out.println("  No se encontró array en la subcategoría: " + subCategory);
                        subCategoryMap.put(subCategory, Collections.emptySet());
                    }
                }

                // Agregar la categoría y sus subcategorías al mapa principal
                lexemesMap.put(category, subCategoryMap);
            } else {
                System.out.println("Categoría no encontrada en el JSON: " + category.getJsonKey());
                // Si no hay subcategorías, se agrega un mapa vacío
                lexemesMap.put(category, Collections.emptyMap());
            }
        }

        // Retornar el mapa con las categorías, subcategorías y sus lexemas
        return lexemesMap;
    }



}
