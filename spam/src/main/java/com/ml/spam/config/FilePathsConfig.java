package com.ml.spam.config;

public class FilePathsConfig {
    /**
     * Se utilizan rutas relativas a  'resources' en classpath (src/main/resources).
     */


    // Ruta para cargar el archivo JSON inicial
    public static final String INITIAL_JSON_PATH = "static/dictionary/initial_spam_vocabulary_base_only.json";

    // Ruta para exportar el diccionario generado en formato JSON
    public static final String EXPORT_DICTIONARY_CREATED_JSON_PATH = "static/dictionary/persisted_initialized_spam_vocabulary_frequenciesZero.json";

    // Ruta para cargar datos en formato CSV
    public static final String CSV_DATA_PATH = "path/to/your/file/data.csv";

    // Ruta para cargar imagen (si es necesario)
    public static final String IMAGE_PATH = "path/to/your/image/image.png";
}
