package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;

public class DictionaryUpdateMain {
    private static final String filePath = FilePathsConfig.EXPORT_DICTIONARY_CREATED_JSON_PATH;


    public static void main(String[] args) {
        SpamDictionaryService service = new SpamDictionaryService();

System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");

        //Mostrar el Map de Dictionary para chequear que estén las categorías vacías
        service.displayDictionary();

        // Mostrar el diccionario persistido para testear el formato del json en consola
        service.displayJsonFileDictionary(filePath);

        // Inicializar el diccionario desde el JSON
        service.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(filePath);

        // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
        service.displayDictionary();



    }

}
