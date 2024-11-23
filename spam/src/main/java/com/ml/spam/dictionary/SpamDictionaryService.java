package com.ml.spam.dictionary;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

    // Métod para inicializar desde JSON
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
        System.out.println(" * === * === Display Map de SpamDictionary * === * ===");
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

    public void displayJsonPersistedDictionary(String filePath) {
        try {
            File file = new File(filePath);

            // Verificar si el archivo existe
            if (!file.exists()) {
                System.out.println("El archivo persistido no existe: " + filePath);
                return;
            }

            // Leer el contenido del archivo
            String jsonContent = new String(Files.readAllBytes(file.toPath()));
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Mostrar el contenido por consola
            System.out.println(" * === * === Display Archivo Json * === * ===");

            System.out.println("=== Diccionario Persistido ===");
            System.out.println();
            System.out.println("=== Palabras de Spam ===");
            System.out.println();
            jsonObject.getJSONObject("onlySpamWords").toMap().forEach((word, freq) ->
                    System.out.println(word + " -> " + freq));
            System.out.println();
            System.out.println("=== Símbolos Raros ===");
            jsonObject.getJSONObject("onlyRareSymbols").toMap().forEach((symbol, freq) ->
                    System.out.println(symbol + " -> " + freq));
            System.out.println();
            System.out.println("=== Stop Words ===");
            jsonObject.getJSONObject("onlyStopWords").toMap().forEach((stopWord, freq) ->
                    System.out.println(stopWord + " -> " + freq));

        } catch (Exception e) {
            System.err.println("Error al leer el archivo persistido: " + e.getMessage());
            e.printStackTrace();
        }
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
    // *  *  *  *  *  *  Actualizacion del diccionario * *  *  *  *  *
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

    // Verifica si una palabra existe en el diccionario
    public boolean wordExists(String word) {
        return dictionary.getOnlySpamWords().containsKey(word) ||
                dictionary.getOnlyStopWords().containsKey(word) ||
                dictionary.getOnlyRareSymbols().containsKey(word);
    }

    // Actualiza la frecuencia de una palabra existente
    public void updateWordFrequency(String word, boolean isSpam) {
        Map<String, Frequency> category = getCategory(word);
        if (category != null) {
            Frequency freq = category.get(word);
            if (isSpam) {
                freq.incrementSpamFrequency();
            } else {
                freq.incrementHamFrequency();
            }
        }
    }


    // Registra una nueva palabra en el mapa de palabras nuevas
    public void addNewWord(String word, boolean isSpam) {
        if (!dictionary.getNewWords().containsKey(word)) {
            dictionary.getNewWords().put(word, new Frequency(isSpam ? 1 : 0, isSpam ? 0 : 1));
        } else {
            Frequency freq = dictionary.getNewWords().get(word);
            if (isSpam) {
                freq.incrementSpamFrequency();
            } else {
                freq.incrementHamFrequency();
            }
        }
    }

    // Determina a qué categoría pertenece una palabra
    private Map<String, Frequency> getCategory(String word) {
        if (dictionary.getOnlySpamWords().containsKey(word)) {
            return dictionary.getOnlySpamWords();
        } else if (dictionary.getOnlyStopWords().containsKey(word)) {
            return dictionary.getOnlyStopWords();
        } else if (dictionary.getOnlyRareSymbols().containsKey(word)) {
            return dictionary.getOnlyRareSymbols();
        }
        return null;
    }
}

