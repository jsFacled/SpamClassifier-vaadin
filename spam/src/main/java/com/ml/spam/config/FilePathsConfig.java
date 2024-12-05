package com.ml.spam.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathsConfig {

    // Rutas relativas dentro de 'src/main/resources'
    public static final String INITIAL_JSON_PATH = "static/dictionary/initial_spam_vocabulary_base_only.json";
    public static final String EXPORT_DICTIONARY_CREATED_JSON_PATH = "static/dictionary/persisted_initialized_spam_vocabulary_frequenciesZero.json";
    public static final String TEST_CSV_DATA_PATH = "static/datasets/mensajes_pruebas.txt";
    public static final String CSV_DATA_PATH = "path/to/your/file/data.csv";
    public static final String IMAGE_PATH = "path/to/your/image/image.png";
}