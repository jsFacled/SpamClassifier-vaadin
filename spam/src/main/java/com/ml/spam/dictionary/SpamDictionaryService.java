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

    public void initializeDictionary(InputStream jsonInputStream) {
        dictionary.initializeFromJson(jsonInputStream);
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
}
