package com.ml.spam.config;

public class FilePathsConfig {

// --- Rutas relativas para Diccionario (src/main/resources/static/dictionary) ---

    // Archivos base del diccionario
  //  public static final String DICTIONARY_CATEGORIZED_WORDS_BASE_JSON_PATH = "static/dictionary/base/categorized_words_base_only.json";
    public static final String DICTIONARY_CATEGORIZED_WORDS_BASE_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWordsOnly.json";
    public static final String DICTIONARY_CATEGORIZED_WORDS_ONLY_BASE_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWordsOnly.json";

    public static final String DICTIONARY_CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH = "static/dictionary/categorizedWords/new_categorized_words_with_frequenciesZero.json";

    // Archivos del repositorio de lexemas
    public static final String DICTIONARY_LEXEMES_REPOSITORY_JSON_PATH = "static/dictionary/lexemesRepository/structured_lexemes_repository.json";
    public static final String DICTIONARY_LEXEMES_CATEGORIES_JSON_PATH = "static/dictionary/lexemesRepository/lexemes_categories.json";
    public static final String DICTIONARY_LEXEME_WORDS_BY_CATEGORY_JSON_PATH = "static/dictionary/lexemesRepository/lexeme_words_by_category.json";

    // Archivos de salida y metadatos del diccionario
    public static final String DICTIONARY_OUTPUT_BASE_JSON_PATH = "static/dictionary/categorizedWords/updatedCategorizedWords.json";
    public static final String DICTIONARY_METADATA_JSON_PATH = "static/dictionary/categorizedWords/dictionary_metadata_2.json";


// --- Rutas para Datasets del Modelo (src/main/resources/static/datasets) ---
    public static final String MODEL_ORIGINAL_TRAIN_MESSAGES_CSV_ESPAÑOL_PATH = "static/datasets/train-mensajesEspañol.csv";
    public static final String MODEL_ORIGINAL_TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH = "static/datasets/originales/test-mensajesEspañol.csv";
    public static final String MODEL_ORIGINAL_CLEANED_TRAIN_MESSAGES_CSV_PATH = "static/datasets/originales/train-mensajesEspañol_cleaned.csv";
    public static final String MODEL_ORIGINAL_CORREOS_SPAM_FAC_TXT_PATH = "static/datasets/originales/correos-spam-fac.txt";

    // Combinación específica: solo mensajes spam entre triple comillas
    public static final String COMBINED_SPAM_TRIPLE_QUOTES_OUTPUT_PATH = "combined_spam_triple_quotes.txt";


// --- Rutas para Pruebas de Código / Desarrollo (src/main/resources/static/datasets) ---

    public static final String TRIAL_MESSAGES_LABELED_CSV_DATA_PATH = "static/datasets/trialMessages/mensajes_pruebas_labeled.csv";


    public static final String IA_GENERATED_TRIPLECUOTES_HAM_PATH = "static/datasets/mensajesInventadosIA/ham_triplecomillas_ia_varios.txt";
    public static final String IA_GENERATED_TRIPLECUOTES_SPAM_PATH = "static/datasets/mensajesInventadosIA/spam_triplecomillas_ia_varios.txt";
    public static final String IA_GENERATED_LABELED_CSV_PATH = "static/datasets/mensajesInventadosIA/Mensajes_varios_label_spam__ham.csv";


// --- Rutas para Datasets con cada tipo de mensajes juntados (src/main/resources/static/datasets/joined) ---
    public static final String JOINED_LABELED_CSV_PATH ="static/datasets/joined/joined_messages_label.csv";
    public static final String JOINED_TRIPLECUOTES_HAM_PATH="static/datasets/joined/joined_messages_triplecomillas_ham.txt";
    public static final String JOINED_TRIPLECUOTES_SPAM_PATH="static/datasets/joined/joined_messages_triplecomillas_spam.txt";

    // --- Rutas para ML Datasets UN único dataset con todos los mensajes juntos y full normalized (src/main/resources/static/mlDatasets) ---
    public static final String MIX_COMBINED_MESSAGES_FUL_NORMALIZED_CSV_PATH ="static/mlDatasets/mix_combined_messages_fullNormalized.csv";
    public static final String FINAL_MLDATASET_NUMERIC_CSV_PATH ="static/mlDatasets/mix_combined_full_dataset.csv";

// --- Constructor privado para evitar instanciación ---

    private FilePathsConfig() {
        // Clase de constantes
    }
}
