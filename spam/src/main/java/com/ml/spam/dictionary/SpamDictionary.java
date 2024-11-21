package com.ml.spam.dictionary;

import org.json.JSONObject;

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

    private final Map<String, Frequency> onlySpamWords = new HashMap<>();
    private final Map<String, Frequency> onlyRareSymbols = new HashMap<>();
    private final Map<String, Frequency> onlyStopWords = new HashMap<>();

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

    public void initializeFromJson(InputStream jsonInputStream) {
        JSONObject jsonObject = new JSONObject(new String(jsonInputStream.readAllBytes()));
        loadCategory(jsonObject.getJSONObject("onlySpamWords"), onlySpamWords);
        loadCategory(jsonObject.getJSONObject("onlyRareSymbols"), onlyRareSymbols);
        loadCategory(jsonObject.getJSONObject("onlyStopWords"), onlyStopWords);
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
}
