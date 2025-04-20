package com.ml.spam.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.spam.dictionary.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.*;
import java.util.function.BiConsumer;
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
    // Método genérico para procesar lexemes
    private static void processStructuredLexemes(
            JSONObject jsonObject,
            BiConsumer<String, JSONArray> lexemeProcessor
    ) {
        for (CharSize charSize : CharSize.values()) {
            JSONObject charSizeJson = jsonObject.optJSONObject(charSize.getJsonKey());
            if (charSizeJson == null) continue;

            for (String lexeme : charSizeJson.keySet()) {
                JSONArray wordsArray = charSizeJson.optJSONArray(lexeme);
                lexemeProcessor.accept(lexeme, wordsArray);
            }
        }
    }

    // Extraer lexemas únicos
    public static Set<String> extractUniqueLexemesFromStructuredLexemes(JSONObject jsonObject) {
        Set<String> uniqueLexemes = new HashSet<>();

        processStructuredLexemes(jsonObject, (lexeme, wordsArray) -> {
            if (lexeme != null && !lexeme.trim().isEmpty()) {
                uniqueLexemes.add(lexeme.trim());
            }
        });

        return uniqueLexemes;
    }

    // Contar palabras por lexema
    public static Map<String, Integer> getLexemeWordCountFromStructuredLexemes(JSONObject jsonObject) {
        Map<String, Integer> lexemeWordCount = new HashMap<>();

        processStructuredLexemes(jsonObject, (lexeme, wordsArray) -> {
            if (wordsArray != null) {
                lexemeWordCount.merge(lexeme, wordsArray.length(), Integer::sum);
            }
        });

        return lexemeWordCount;
    }

    // Obtener lexemas con sus palabras
    public static Map<String, List<String>> getLexemesWithWordsFromStructuredLexemes(JSONObject jsonObject) {
        Map<String, List<String>> lexemesWithWords = new HashMap<>();

        for (CharSize charSize : CharSize.values()) {
            JSONObject charSizeJson = jsonObject.optJSONObject(charSize.getJsonKey());
            if (charSizeJson == null) continue;

            for (String lexeme : charSizeJson.keySet()) {
                JSONArray wordsArray = charSizeJson.optJSONArray(lexeme);
                if (wordsArray == null) continue;

                List<String> wordsList = new ArrayList<>();
                for (int i = 0; i < wordsArray.length(); i++) {
                    String word = wordsArray.optString(i, "").trim();
                    if (!word.isEmpty()) {
                        wordsList.add(word);
                    }
                }

                lexemesWithWords.merge(lexeme, wordsList, (existing, newWords) -> {
                    existing.addAll(newWords);
                    return existing;
                });
            }
        }
        return lexemesWithWords;
    }


    public static ValidationResult validateStructuredLexemesRepository(JSONObject jsonObject) {
        Set<String> uniqueWords = new HashSet<>();
        Set<String> duplicateWords = new HashSet<>();
        boolean isValidStructure = true;
        List<String> errors = new ArrayList<>();

        for (CharSize charSize : CharSize.values()) {
            JSONObject charSizeJson = jsonObject.optJSONObject(charSize.getJsonKey());
            if (charSizeJson == null) {
                errors.add("CharSize '" + charSize.getJsonKey() + "' está ausente o no es un objeto válido.");
                isValidStructure = false;
                continue;
            }

            for (String lexeme : charSizeJson.keySet()) {
                JSONArray wordsArray = charSizeJson.optJSONArray(lexeme);
                if (wordsArray == null) {
                    errors.add("Lexeme '" + lexeme + "' en CharSize '" + charSize.getJsonKey() + "' no contiene un arreglo de palabras.");
                    isValidStructure = false;
                    continue;
                }

                for (int i = 0; i < wordsArray.length(); i++) {
                    String word = wordsArray.optString(i, "").trim();
                    if (word.isEmpty()) {
                        errors.add("Palabra vacía o inválida encontrada en lexeme '" + lexeme + "' dentro de CharSize '" + charSize.getJsonKey() + "'.");
                        isValidStructure = false;
                    } else if (!uniqueWords.add(word)) {
                        duplicateWords.add(word);
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            System.err.println("[ERROR] Problemas detectados en la validación:");
            errors.forEach(System.err::println);
        }

        return new ValidationResult(isValidStructure, duplicateWords);
    }



    public static void removeInvalidDuplicatesByCharSize(JSONObject jsonObject) {
        for (CharSize charSize : CharSize.values()) {
            JSONObject charSizeJson = jsonObject.optJSONObject(charSize.getJsonKey());
            if (charSizeJson == null) continue;

            int expectedSize = charSize.getSize();

            for (String lexeme : charSizeJson.keySet()) {
                JSONArray wordsArray = charSizeJson.optJSONArray(lexeme);
                if (wordsArray == null) continue;

                JSONArray validWordsArray = new JSONArray();
                List<String> removedWords = new ArrayList<>();

                for (int i = 0; i < wordsArray.length(); i++) {
                    String word = wordsArray.optString(i, "").trim();
                    if (!word.isEmpty() && (expectedSize == -1 || word.length() == expectedSize)) {
                        validWordsArray.put(word);
                    } else {
                        removedWords.add(word);
                    }
                }

                charSizeJson.put(lexeme, validWordsArray);

                if (!removedWords.isEmpty()) {
                    System.out.println("[INFO] Palabras eliminadas del lexema '" + lexeme + "' en CharSize '"
                            + charSize.getJsonKey() + "': " + removedWords);
                }
            }
        }
        System.out.println("[INFO] Proceso de eliminación de duplicados completado.");
    }


    public static Map<String, List<String>> extractCategorizedWords(JSONObject jsonObject) {
        Map<String, List<String>> categorizedWords = new HashMap<>();

        for (WordCategory category : WordCategory.values()) {
            String categoryKey = category.getJsonKey();
            if (jsonObject.has(categoryKey)) {
                Object value = jsonObject.get(categoryKey);
                if (value instanceof JSONObject) {
                    // Si es un JSONObject, extraer las claves como palabras
                    JSONObject categoryObject = (JSONObject) value;
                    List<String> words = new ArrayList<>(categoryObject.keySet());
                    categorizedWords.put(categoryKey, words);
                } else {
                    System.out.println("[WARN] El valor de " + categoryKey + " no es un JSONObject.");
                }
            } else {
                System.out.println("[WARN] Categoría no encontrada en el JSON: " + categoryKey);
            }
        }

        return categorizedWords;
    }

    public static JSONObject spamDictionaryMetadataToJson(SpamDictionaryMetadata metadata) {
        JSONObject json = new JSONObject();

        // Metadatos globales
        json.put("totalInstances", metadata.getTotalInstances());
        json.put("totalHam", metadata.getTotalHam());
        json.put("totalSpam", metadata.getTotalSpam());
        json.put("totalDatasetsProcessed", metadata.getTotalDatasetsProcessed());

        // Nuevo campo: nombre del archivo de diccionario exportado
        json.put("exportedDictionaryFileName", metadata.getExportedDictionaryFileName());

        // Detalle de datasets
        JSONArray datasetArray = new JSONArray();
        for (DatasetMetadata d : metadata.getDatasetDetails()) {
            JSONObject dJson = new JSONObject();
            dJson.put("id", d.getId());
            dJson.put("instances", d.getInstances());
            dJson.put("ham", d.getHam());
            dJson.put("spam", d.getSpam());
            dJson.put("timestamp", d.getTimestamp());
            datasetArray.put(dJson);
        }

        json.put("datasetDetails", datasetArray);

        return json;
    }

    public static SpamDictionaryMetadata jsonToSpamDictionaryMetadata(JSONObject json) {
        // Lógica para mapear el JSON al objeto SpamDictionaryMetadata
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json.toString(), SpamDictionaryMetadata.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir JSON a SpamDictionaryMetadata: " + e.getMessage(), e);
        }
    }

    public static void validateMetadataJsonStructure(JSONObject json) {
        if (json == null) {
            throw new IllegalArgumentException("El JSON de metadatos es nulo.");
        }

        List<String> requiredFields = Arrays.asList(
                "totalDatasetsProcessed",
                "totalInstances",
                "totalHam",
                "totalSpam",
                "datasetDetails",
                "exportedDictionaryFileName"
        );

        for (String field : requiredFields) {
            if (!json.has(field)) {
                throw new IllegalArgumentException("Falta el campo obligatorio en metadata JSON: " + field);
            }
        }

        if (!json.get("datasetDetails").toString().startsWith("[")) {
            throw new IllegalArgumentException("El campo 'datasetDetails' debe ser un arreglo JSON.");
        }
    }

}
