package com.ml.spam.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DictionaryMain {
    public static void main(String[] args) throws IOException {


        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        // * * * * //


        String filePath = "spam/src/main/resources/static/persisted_initialized_spam_vocabulary_frequenciesZero.json";

        // Mostrar el diccionario persistido
        service.displayJsonPersistedDictionary(filePath);
    }
}
