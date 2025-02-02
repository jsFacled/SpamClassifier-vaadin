package com.ml.spam.dictionary.stageMain;

import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;

import java.io.IOException;

public class DictionaryMain {

    public static void main(String[] args) throws IOException {


        SpamDictionary dictionary = SpamDictionary.getInstance();
        ResourcesHandler resourcesHandler = new ResourcesHandler();
        SpamDictionaryService service = new SpamDictionaryService();

        // * * * * //

    // String filePath = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\persisted_initialized_spam_vocabulary_frequenciesZero111.json";


     String filePath = "static/dictionary/categorized_words_vocabulary_base_only.json";

        // Mostrar el diccionario persistido
       // service.displayJsonFileDictionary(filePath);

        //Mostrar el json que extrae resourcehandler
        System.out.println(
                //resourcesHandler.loadResourceAsString(filePath)
                 resourcesHandler.loadJson(filePath)

        );


   //mostrar dictionary
        service.displayCategorizedWordsInDictionary();
    }


}
