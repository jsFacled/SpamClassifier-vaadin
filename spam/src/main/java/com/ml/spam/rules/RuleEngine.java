package com.ml.spam.rules;

import com.ml.spam.dictionary.SpamDictionary;

public class RuleEngine {
    public void applyRules(SpamDictionary dictionary) {
        dictionary.getNewWords().forEach((word, frequency) -> {
            // Clasificar automáticamente palabras nuevas en categorías
            if (isStopWord(word)) {
                dictionary.getOnlyStopWords().put(word, frequency);
            } else if (isRareSymbol(word)) {
                dictionary.getOnlyRareSymbols().put(word, frequency);
            } else {
                dictionary.getOnlySpamWords().put(word, frequency);
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
