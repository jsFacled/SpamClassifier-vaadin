package com.ml.spam.dictionary;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
* Clase Singleton que almacena los mapas del diccionario (spamWords, rareSymbols, stopWords)
* y ofrece métodos para inicializarlos desde un archivo JSON.
* Métodos para inicializar mapas desde un archivo JSON.
* Utiliza un Frequency simple para almacenar las frecuencias de spam y ham.
 */
public class SpamDictionary {
    private static final SpamDictionary instance = new SpamDictionary();

    //Hay 4 categorías
    private final Map<String, Frequency> spamWords = new HashMap<>();
    private final Map<String, Frequency> rareSymbols = new HashMap<>();
    private final Map<String, Frequency> stopWords = new HashMap<>();
    private final Map<String, Frequency> newWords = new HashMap<>();

    private SpamDictionary() {}

    public static SpamDictionary getInstance() {
        return instance;
    }

    public Map<String, Frequency> getSpamWords() {
        return spamWords;
    }

    public Map<String, Frequency> getRareSymbols() {
        return rareSymbols;
    }

    public Map<String, Frequency> getStopWords() {
        return stopWords;
    }

    public Map<String, Frequency> getNewWords() {
        return newWords;
    }

    public void initializeCategory(Map<String, Frequency> targetMap, Iterable<String> words) {
        words.forEach(word -> targetMap.put(word, new Frequency(0, 0)));
    }

    //Ver estos metods

    public void initializeFromJson(JSONObject jsonObject) {
        loadCategory(jsonObject.getJSONObject("spamWords"), spamWords);
        loadCategory(jsonObject.getJSONObject("rareSymbols"), rareSymbols);
        loadCategory(jsonObject.getJSONObject("stopWords"), stopWords);
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spamWords", categoryToJson(spamWords));
        jsonObject.put("rareSymbols", categoryToJson(rareSymbols));
        jsonObject.put("stopWords", categoryToJson(stopWords));
        return jsonObject;
    }
    private void loadCategory(JSONObject jsonCategory, Map<String, Frequency> targetMap) {
        jsonCategory.keys().forEachRemaining(word -> {
            JSONObject freqData = jsonCategory.getJSONObject(word);
            targetMap.put(word, new Frequency(
                    freqData.getInt("spamFrequency"),
                    freqData.getInt("hamFrequency")
            ));
        });
    }

    private JSONObject categoryToJson(Map<String, Frequency> category) {
        JSONObject jsonCategory = new JSONObject();
        category.forEach((word, frequency) -> {
            JSONObject freqData = new JSONObject();
            freqData.put("spamFrequency", frequency.getSpamFrequency());
            freqData.put("hamFrequency", frequency.getHamFrequency());
            jsonCategory.put(word, freqData);
        });
        return jsonCategory;
    }
}
