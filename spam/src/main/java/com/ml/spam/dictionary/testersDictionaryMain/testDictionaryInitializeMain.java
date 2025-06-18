package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.JsonDebugHelper;

public class testDictionaryInitializeMain {

    private static final String catWordsPath = FilePathsConfig.DICTIONARY_CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH;
    public static void main(String[] args) {
        SpamDictionaryService service = new SpamDictionaryService();
        JsonDebugHelper jsonDebugHelper = new JsonDebugHelper();
        ResourcesHandler rh = new ResourcesHandler();

        System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");

        //Mostrar el Map de Dictionary para chequear que estén las categorías vacías
        service.displayCategorizedWordsInDictionary();

        // Mostrar el diccionario persistido para testear el formato del json en consola
        service.displayJsonFileDictionary(catWordsPath);

        // Inicializar el diccionario desde el JSON
        service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(catWordsPath, lexemePath);

        // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
        service.displayCategorizedWordsInDictionary();
JsonDebugHelper.debugJsonLoad(catWordsPath, rh);


    }
}
