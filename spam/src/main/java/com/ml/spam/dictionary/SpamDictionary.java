package com.ml.spam.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *Rol:
 *      Clase Singleton que gestiona las palabras y sus frecuencias en el diccionario.
 *
 * Responsabilidades:
 *      Almacenar palabras relevantes en un mapa.
 *      Identificar "stop words" y símbolos raros.
 *      Agregar o actualizar palabras en el diccionario.
 */


public class SpamDictionary {
    private static final SpamDictionary instance = new SpamDictionary();

    private final Map<String, WordData> wordSpam = new HashMap<>();
    private final Set<String> rareSymbols = Set.of("!", "$", "%", "&", "*", "#", "_", "@", "?");
    private final Set<String> stopWords = Set.of(
            "a", "al", "algo", "algunos", "algunas", "así", "aunque", "con", "cómo", "cuándo", "dónde",
            "de", "del", "desde", "el", "él", "ella", "ellas", "ellos", "en", "entre", "esa", "esas",
            "ese", "esos", "esta", "estas", "este", "estos", "fue", "fui", "fuera", "fueron", "ha",
            "había", "habían", "hasta", "hay", "he", "hemos", "la", "las", "le", "les", "lo", "los",
            "más", "me", "mi", "mí", "mis", "muy", "ni", "no", "nos", "nosotros", "o", "para", "pero",
            "por", "porque", "qué", "que", "quien", "quienes", "se", "será", "sí", "sin", "sobre",
            "sólo", "su", "sus", "también", "tan", "tanto", "te", "ti", "tiene", "tienen", "todo",
            "todos", "tu", "tus", "tú", "un", "una", "unas", "uno", "unos", "ya", "yo"
    );

    private SpamDictionary() {}

    public static SpamDictionary getInstance() {
        return instance;
    }

    public Map<String, WordData> getWordSpam() {
        return wordSpam;
    }

    public boolean isRareSymbol(String symbol) {
        return rareSymbols.contains(symbol);
    }

    public boolean isStopWord(String word) {
        return stopWords.contains(word);
    }

    public void addOrUpdateWord(String word, boolean isSpam) {
        WordData wordData = wordSpam.getOrDefault(word, new WordData(word, 0, 0));
        if (isSpam) {
            wordData.incrementSpamFrequency();
        } else {
            wordData.incrementHamFrequency();
        }
        wordSpam.put(word, wordData);
    }
}
