package com.ml.spam.dictionary;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/*
Clase de servicio que interactúa con SpamDictionary para inicialización, manipulación, y exportación de datos.
Métod initializeDictionary para cargar el diccionario desde un JSON.
Métod exportToJson para guardar el estado del diccionario en un archivo JSON.

 */
public class SpamDictionaryService {
    private final SpamDictionary dictionary;

    public SpamDictionaryService(SpamDictionary dictionary) {
        this.dictionary = dictionary;
    }

    // Método para inicializar desde JSON
   /* public void initializeFromJson(InputStream jsonInputStream) throws IOException {
        JSONObject jsonObject = new JSONObject(new String(jsonInputStream.readAllBytes()));
        dictionary.initializeFromJson(jsonObject);
    }

    */

    public void initializeFromJson(InputStream inputStream) {
        try {
            // Leer JSON desde InputStream
            String jsonString = new String(inputStream.readAllBytes());
            JSONObject jsonObject = new JSONObject(jsonString);

            // Inicializar las categorías
            dictionary.initializeCategory(dictionary.getOnlySpamWords(),
                    jsonObject.getJSONArray("onlySpamWords").toList().stream().map(Object::toString).toList());
            dictionary.initializeCategory(dictionary.getOnlyRareSymbols(),
                    jsonObject.getJSONArray("onlyRareSymbols").toList().stream().map(Object::toString).toList());
            dictionary.initializeCategory(dictionary.getOnlyStopWords(),
                    jsonObject.getJSONArray("onlyStopWords").toList().stream().map(Object::toString).toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el diccionario: " + e.getMessage(), e);
        }
    }
    public void exportToJson(String filePath) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("onlySpamWords", exportCategory(dictionary.getOnlySpamWords()));
        jsonObject.put("onlyRareSymbols", exportCategory(dictionary.getOnlyRareSymbols()));
        jsonObject.put("onlyStopWords", exportCategory(dictionary.getOnlyStopWords()));

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonObject.toString(4));
        }
    }

    private JSONObject exportCategory(Map<String, Frequency> category) {
        JSONObject jsonCategory = new JSONObject();
        category.forEach((word, freq) -> {
            JSONObject freqData = new JSONObject();
            freqData.put("spamFrequency", freq.getSpamFrequency());
            freqData.put("hamFrequency", freq.getHamFrequency());
            jsonCategory.put(word, freqData);
        });
        return jsonCategory;
    }

    public void displayDictionary() {
        System.out.println("=== Palabras de Spam ===");
        dictionary.getOnlySpamWords().forEach((word, freq) ->
                System.out.println(word + " -> " + freq));
        System.out.println("=== Símbolos Raros ===");
        dictionary.getOnlyRareSymbols().forEach((symbol, freq) ->
                System.out.println(symbol + " -> " + freq));
        System.out.println("=== Stop Words ===");
        dictionary.getOnlyStopWords().forEach((stopWord, freq) ->
                System.out.println(stopWord + " -> " + freq));
    }

}
