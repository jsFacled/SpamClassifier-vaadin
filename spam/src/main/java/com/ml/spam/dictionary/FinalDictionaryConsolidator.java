package com.ml.spam.dictionary;

import com.ml.spam.dictionary.models.SpamDictionary;

public class FinalDictionaryConsolidator {
    public void consolidateNewWords(SpamDictionary dictionary) {
        dictionary.getNewWords().forEach((word, frequency) -> {
            // Lógica para mover palabras nuevas a las categorías correspondientes
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
        // Implementar lógica para identificar stopwords
        return false;
    }

    private boolean isRareSymbol(String word) {
        // Implementar lógica para identificar símbolos raros
        return false;
    }
}
