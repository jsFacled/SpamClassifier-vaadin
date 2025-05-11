package com.ml.spam.datasetProcessor.metadata;

import com.ml.spam.handlers.ResourcesHandler;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LexemeWordMetadata {

    private final Map<String, Integer> lexemeWordCounts = new HashMap<>();

    public LexemeWordMetadata(String jsonPath) {
        loadFromResources(jsonPath);
    }

    private void loadFromResources(String path) {
        JSONObject root = new ResourcesHandler().loadJson(path);

        for (Iterator<String> it = root.keys(); it.hasNext(); ) {
            String lexeme = it.next();
            JSONObject entry = root.getJSONObject(lexeme);
            int count = entry.optInt("count", 0);
            lexemeWordCounts.put(lexeme, count);
        }
    }

    public int getWordCountForLexeme(String lexeme) {
        return lexemeWordCounts.getOrDefault(lexeme, 0);
    }

    public boolean containsLexeme(String lexeme) {
        return lexemeWordCounts.containsKey(lexeme);
    }
}
