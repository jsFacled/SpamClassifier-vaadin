package com.ml.spam.dictionary.testersDictionaryMain;
import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;

public class InitializeCategorizedWordsTest {

    private static final String categorizedWordsJsonPath = FilePathsConfig.DICTIONARY_CATEGORIZED_WORDS_BASE_JSON_PATH;

    public static void main(String[] args) {
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 1: Inicialización de Categorized Words  === /  /   /   /   /   /   /   /   /   /   === \n");

        // Inicializar categorizedWords desde el archivo JSON
      service.initializeCategorizedWordsFromJsonPath(categorizedWordsJsonPath);

        // Mostrar los Map de SpamDictionary para chequear que se haya inicializado correctamente
        service.displayCategorizedWordsInDictionary();
    }
}
