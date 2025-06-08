package com.ml.spam.config;

public class FilePathsConfig {

    // Rutas relativas dentro de 'src/main/resources/static/dictionary'
  //  public static final String CATEGORIZED_WORDS_BASE_JSON_PATH = "static/dictionary/base/categorized_words_base_only.json";
    public static final String CATEGORIZED_WORDS_BASE_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWordsOnly.json";

    public static final String CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH = "static/dictionary/categorizedWords/new_categorized_words_with_frequenciesZero.json";


    public static final String LEXEMES_REPOSITORY_JSON_PATH = "static/dictionary/lexemesRepository/structured_lexemes_repository.json";
    public static final String LEXEMES_CATEGORIES_JSON_PATH = "static/dictionary/lexemesRepository/lexemes_categories.json";
    public static final String LEXEME_WORDS_BY_CATEGORY_JSON_PATH = "static/dictionary/lexemesRepository/lexeme_words_by_category.json";

    // Rutas relativas dentro de 'src/main/resources/static/datasets'
    public static final String PRUEBA_CSV_DATA_PATH = "static/datasets/forDictionaryTest/mensajes_pruebas.csv";
    public static final String TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH = "static/datasets/test-mensajesEspañol.csv";
    public static final String TRAIN_MESSAGES_CSV_ESPAÑOL_DATA_PATH = "static/datasets/train-mensajesEspañol.csv";
    public static final String CORREOS_SPAM_FAC_TXT_PATH = "static/datasets/correos-spam-fac.txt";
    public static final String CLEANED_TRAIN_MESSAGES_CSV_PATH = "static/datasets/train-mensajesEspañol_cleaned.csv";


    public static final String CATEGORIZED_WORDS_ONLY_BASE_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWordsOnly.json";
    public static final String BASE_OUTPUT_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWords.json";
    public static final String DICTIONARY_METADATA_JSON_PATH = "static/dictionary/categorizedWords/dictionary_metadata_2.json";

    public static final String IA_HAM_TXT_PATH = "static/datasets/mensajesInventadosIA/ham_triplecomillas_ia_varios.txt";
    public static final String IA_SPAM_TXT_PATH = "static/datasets/mensajesInventadosIA/spam_triplecomillas_ia_varios.txt";
    public static final String IA_OTROS_CSV_PATH = "static/datasets/mensajesInventadosIA/Mensajes_varios_label_spam__ham.csv";

}
