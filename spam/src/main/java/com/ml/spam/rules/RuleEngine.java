package com.ml.spam.rules;

import com.ml.spam.dictionary.SpamDictionary;

public class RuleEngine {
    public void applyRules(SpamDictionary dictionary) {
        dictionary.getNewWords().forEach((word, frequency) -> {
            // Clasificar automáticamente palabras nuevas en categorías
            if (isStopWord(word)) {
                dictionary.getStopWords().put(word, frequency);
            } else if (isRareSymbol(word)) {
                dictionary.getRareSymbols().put(word, frequency);
            } else {
                dictionary.getSpamWords().put(word, frequency);
            }
        });
        dictionary.getNewWords().clear();
    }

    private boolean isStopWord(String word) {
        // Lógica para identificar stopwords
        return false;
    }

    private boolean isRareSymbol(String word) {
        // Lógica para identificar símbolos raros
        return false;
    }
}
