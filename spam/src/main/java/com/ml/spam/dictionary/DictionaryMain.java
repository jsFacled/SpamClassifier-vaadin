package com.ml.spam.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DictionaryMain {
    public static void main(String[] args) throws IOException {
        System.out.println("Persistiendo diccionario...");

        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        // * * * * //


        String filePath = "spam/src/main/resources/static/spam_vocabulary_initialized_persisted.json";

        // Mostrar el diccionario persistido
        service.displayJsonPersistedDictionary(filePath);
    }
}
