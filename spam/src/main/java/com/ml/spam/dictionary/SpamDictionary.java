package com.ml.spam.dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpamDictionary {
    // Instancia única para el patrón Singleton
    private static final SpamDictionary instance = new SpamDictionary();

    // Diccionario de palabras relevantes
    private final Map<String, WordData> wordSpam = new HashMap<>();

    // Conjunto de símbolos raros
    private final Set<String> rareSymbols = Set.of("!", "$", "%", "&", "*", "#", "_", "@", "?");

    // Conjunto de palabras comunes (stop words)
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

    // Constructor privado para evitar instanciación externa
    private SpamDictionary() {}

    // Método para obtener la instancia única de SpamDictionary
    public static SpamDictionary getInstance() {
        return instance;
    }

    // Método para acceder al diccionario de palabras relevantes
    public Map<String, WordData> getWordSpam() {
        return wordSpam;
    }

    // Método para verificar si un símbolo es considerado raro
    public boolean isRareSymbol(String symbol) {
        return rareSymbols.contains(symbol);
    }

    // Método para acceder al conjunto de símbolos raros
    public Set<String> getRareSymbols() {
        return rareSymbols;
    }

    // Método para verificar si una palabra es una stop word
    public boolean isStopWord(String word) {
        return stopWords.contains(word);
    }

    // Método para acceder al conjunto de stop words
    public Set<String> getStopWords() {
        return stopWords;
    }

    // Método para agregar o actualizar palabras en el diccionario
    public void addOrUpdateWord(String word, boolean isSpam) {
        WordData wordData = wordSpam.getOrDefault(word, new WordData(word, 0, 0));
        if (isSpam) {
            wordData.incrementSpamFrequency();
        } else {
            wordData.incrementHamFrequency();
        }
        wordSpam.put(word, wordData);
    }

    // Método para inicializar el diccionario desde una lista de palabras
    public void initializeFromList(Set<String> initialWords) {
        for (String word : initialWords) {
            if (!wordSpam.containsKey(word)) {
                wordSpam.put(word, new WordData(word, 0, 0));
            }
        }
    }
}
