package com.ml.spam.dictionary;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;

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
