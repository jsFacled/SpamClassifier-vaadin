package com.ml.spam.dictionary;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/*
* Clase Singleton que almacena los mapas del diccionario (onlySpamWords, onlyRareSymbols, onlyStopWords)
* y ofrece métodos para inicializarlos desde un archivo JSON.
* Métodos para inicializar mapas desde un archivo JSON.
* Utiliza un Frequency simple para almacenar las frecuencias de spam y ham.
 */
public class SpamDictionary {
    private static final SpamDictionary instance = new SpamDictionary();

    //Hay 3 categorías: SpamWords, RareSymbols y StopWords
    private final Map<String, Frequency> onlySpamWords = new HashMap<>();
    private final Map<String, Frequency> onlyRareSymbols = new HashMap<>();
    private final Map<String, Frequency> onlyStopWords = new HashMap<>();
    private final Map<String, Frequency> newWords = new HashMap<>();

    private SpamDictionary() {}

    public static SpamDictionary getInstance() {
        return instance;
    }

    public Map<String, Frequency> getOnlySpamWords() {
        return onlySpamWords;
    }

    public Map<String, Frequency> getOnlyRareSymbols() {
        return onlyRareSymbols;
    }

    public Map<String, Frequency> getOnlyStopWords() {
        return onlyStopWords;
    }

    public Map<String, Frequency> getNewWords() {
        return newWords;
    }

    public void initializeCategory(Map<String, Frequency> targetMap, Iterable<String> words) {
        words.forEach(word -> targetMap.put(word, new Frequency(0, 0)));
    }

    //Ver estos metods

    public void initializeFromJson(JSONObject jsonObject) {
        loadCategory(jsonObject.getJSONObject("onlySpamWords"), onlySpamWords);
        loadCategory(jsonObject.getJSONObject("onlyRareSymbols"), onlyRareSymbols);
        loadCategory(jsonObject.getJSONObject("onlyStopWords"), onlyStopWords);
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("onlySpamWords", categoryToJson(onlySpamWords));
        jsonObject.put("onlyRareSymbols", categoryToJson(onlyRareSymbols));
        jsonObject.put("onlyStopWords", categoryToJson(onlyStopWords));
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
