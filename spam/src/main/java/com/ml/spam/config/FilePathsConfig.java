package com.ml.spam.config;

public class FilePathsConfig {

    // Rutas relativas dentro de 'src/main/resources/static/dictionary'
  //  public static final String CATEGORIZED_WORDS_BASE_JSON_PATH = "static/dictionary/base/categorized_words_base_only.json";
    public static final String CATEGORIZED_WORDS_BASE_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWordsOnly.json";

    public static final String CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH = "static/dictionary/categorizedWords/new_categorized_words_with_frequenciesZero.json";


    public static final String ACCENTED_PAIRS_JSON_PATH = "static/dictionary/accentedPairs/accented_pairs.json";

    public static final String LEXEMES_REPOSITORY_JSON_PATH = "static/dictionary/lexemesRepository/structured_lexemes_repository.json";
    public static final String LEXEMES_CATEGORIES_JSON_PATH = "static/dictionary/lexemesRepository/lexemes_categories.json";
    public static final String LEXEME_WORDS_BY_CATEGORY_JSON_PATH = "static/dictionary/lexemesRepository/lexeme_words_by_category.json";

    // Rutas relativas dentro de 'src/main/resources/static/datasets'
    public static final String PRUEBA_CSV_DATA_PATH = "static/datasets/mensajes_pruebas.txt";
    public static final String TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH = "static/datasets/test-mensajesEspañol.csv";
    public static final String TRAIN_MESSAGES_CSV_ESPAÑOL_DATA_PATH = "static/datasets/train-mensajesEspañol.csv";
    public static final String CORREOS_SPAM_FAC_TXT_PATH = "static/datasets/correos-spam-fac.txt";


    public static final String BASE_OUTPUT_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWords.json";
    public static final String CATEGORIZED_WORDS_ONLY_BASE_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWordsOnly.json";

}
