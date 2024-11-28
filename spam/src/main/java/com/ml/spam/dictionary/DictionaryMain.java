package com.ml.spam.dictionary;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;

import java.io.IOException;

public class DictionaryMain {

    public static void main(String[] args) throws IOException {


        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        // * * * * //

    // String filePath = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\persisted_initialized_spam_vocabulary_frequenciesZero111.json";

     String filePath = "static/persisted_initialized_spam_vocabulary_frequenciesZero111.json";

        // Mostrar el diccionario persistido
        service.displayJsonFileDictionary(filePath);
    }


}
