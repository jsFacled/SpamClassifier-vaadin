package com.ml.spam.config;

public class FilePathsConfig {

    // Rutas relativas dentro de 'src/main/resources/static/dictionary'
    public static final String CATEGORIZED_WORDS_BASE_JSON_PATH = "static/dictionary/categorized_words_vocabulary_base_only.json";
    public static final String CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH = "static/dictionary/categorized_words_with_frequenciesZero.json";

    public static final String ACCENTED_PAIRS_JSON_PATH = "static/dictionary/accented_pairs.json";

    public static final String LEXEMES_JSON_PATH = "static/dictionary/lexemes.json";
       public static final String LEXEMES_REPOSITORY_JSON_PATH = "static/dictionary/lexemes_repository.json";

    // Rutas relativas dentro de 'src/main/resources/static/datasets'
    public static final String TEST_CSV_DATA_PATH = "static/datasets/mensajes_pruebas.txt";
    public static final String TEST_CSV_ESPAÑOL_DATA_PATH = "static/datasets/test-mensajesEspañol.csv";

    // Otros archivos genéricos (si son necesarios)
    public static final String CSV_DATA_PATH = "path/to/your/file/data.csv";
    public static final String IMAGE_PATH = "path/to/your/image/image.png";
}
