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

    public Map<String, Frequency> getCategory(String category) {
        switch (category){
            case
        }

        return dictionary.gcategory;
    }

    public void initializeFromJson(InputStream inputStream) {
        try {
            // Leer JSON desde InputStream
            String jsonString = new String(inputStream.readAllBytes());
            JSONObject jsonObject = new JSONObject(jsonString);

            // Inicializar las categorías
            dictionary.initializeCategory(dictionary.getSpamWords(),
                    jsonObject.getJSONArray("onlySpamWords").toList().stream().map(Object::toString).toList());
            dictionary.initializeCategory(dictionary.getRareSymbols(),
                    jsonObject.getJSONArray("onlyRareSymbols").toList().stream().map(Object::toString).toList());
            dictionary.initializeCategory(dictionary.getStopWords(),
                    jsonObject.getJSONArray("onlyStopWords").toList().stream().map(Object::toString).toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el diccionario: " + e.getMessage(), e);
        }
    }
    public void exportToJson(String filePath) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("onlySpamWords", exportCategory(dictionary.getSpamWords()));
        jsonObject.put("onlyRareSymbols", exportCategory(dictionary.getRareSymbols()));
        jsonObject.put("onlyStopWords", exportCategory(dictionary.getStopWords()));

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
        dictionary.getSpamWords().forEach((word, freq) ->
                System.out.println(word + " -> " + freq));
        System.out.println("=== Símbolos Raros ===");
        dictionary.getRareSymbols().forEach((symbol, freq) ->
                System.out.println(symbol + " -> " + freq));
        System.out.println("=== Stop Words ===");
        dictionary.getStopWords().forEach((stopWord, freq) ->
                System.out.println(stopWord + " -> " + freq));
    }


    //El filePath debe estar en:
    // String filePath = "spam/src/main/resources/static/persisted_initialized_spam_vocabulary_frequenciesZero.json";
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

    public void loadFromJsonAndReplace(InputStream inputStream) {
        try {
            // Leer JSON desde InputStream
            String jsonString = new String(inputStream.readAllBytes());
            JSONObject jsonObject = new JSONObject(jsonString);

            // Limpia los mapas actuales
            dictionary.getSpamWords().clear();
            dictionary.getRareSymbols().clear();
            dictionary.getStopWords().clear();
            dictionary.getNewWords().clear();

            // Carga las categorías desde el JSON
            dictionary.initializeFromJson(jsonObject);

            System.out.println("Diccionario reemplazado con los datos del JSON.");
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar el diccionario: " + e.getMessage(), e);
        }
    }

    /*
    //Leer un json y actualizar el diccionario actual con estas palabras mezclando, o sea agregando las que
    //no están y sumando las que ya están.
    public void mergeFromJson(InputStream inputStream) {
        try {
            // Leer JSON desde InputStream
            String jsonString = new String(inputStream.readAllBytes());
            JSONObject jsonObject = new JSONObject(jsonString);

            // Fusionar las categorías con las existentes
            mergeCategory(jsonObject.getJSONObject("spamWords"), dictionary.getSpamWords());
            mergeCategory(jsonObject.getJSONObject("rareSymbols"), dictionary.getRareSymbols());
            mergeCategory(jsonObject.getJSONObject("stopWords"), dictionary.getStopWords());

            System.out.println("Diccionario fusionado con los datos del JSON.");
        } catch (Exception e) {
            throw new RuntimeException("Error al fusionar el diccionario: " + e.getMessage(), e);
        }
    }
*/
    /*
    //Métod para mezclar palabras que ya están con nuevas.
    private void mergeCategory(JSONObject jsonCategory, Map<String, Frequency> targetMap) {
        jsonCategory.keys().forEachRemaining(word -> {
            JSONObject freqData = jsonCategory.getJSONObject(word);
            Frequency newFrequency = new Frequency(
                    freqData.getInt("spamFrequency"),
                    freqData.getInt("hamFrequency")
            );
            targetMap.merge(word, newFrequency, (existingFreq, newFreq) -> {
                existingFreq.incrementSpamFrequency(newFreq.getSpamFrequency());
                existingFreq.incrementHamFrequency(newFreq.getHamFrequency());
                return existingFreq;
            });
        });
    }
*/

    // Verifica si una palabra existe en el diccionario
    public boolean wordExists(String word) {
        return dictionary.getSpamWords().containsKey(word) ||
                dictionary.getStopWords().containsKey(word) ||
                dictionary.getRareSymbols().containsKey(word);
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
        if (dictionary.getSpamWords().containsKey(word)) {
            return dictionary.getSpamWords();
        } else if (dictionary.getStopWords().containsKey(word)) {
            return dictionary.getStopWords();
        } else if (dictionary.getRareSymbols().containsKey(word)) {
            return dictionary.getRareSymbols();
        }
        return null;
    }
}

