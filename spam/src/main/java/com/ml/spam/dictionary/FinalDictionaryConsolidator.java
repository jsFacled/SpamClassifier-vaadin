package com.ml.spam.dictionary;

public class FinalDictionaryConsolidator {
    public void consolidateNewWords(SpamDictionary dictionary) {
        dictionary.getNewWords().forEach((word, frequency) -> {
            // Lógica para mover palabras nuevas a las categorías correspondientes
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
        // Implementar lógica para identificar stopwords
        return false;
    }

    private boolean isRareSymbol(String word) {
        // Implementar lógica para identificar símbolos raros
        return false;
    }
}
