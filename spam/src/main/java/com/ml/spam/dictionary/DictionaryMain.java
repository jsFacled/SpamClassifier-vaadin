package com.ml.spam.dictionary;

import java.io.IOException;
import java.io.InputStream;

public class DictionaryMain {
    public static void main(String[] args) throws IOException {
        System.out.println("Persistiendo diccionario...");

        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        service.exportToJson("spam/src/main/resources/static/spam_vocabulary_persisted.json");
        System.out.println("Diccionario exportado correctamente a spam_vocabulary_persisted.json");

    }
}
