package com.ml.spam.dictionary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.dictionary.models.*;
import com.ml.spam.dictionary.reports.DictionarySummaryReport;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.JsonUtils;
import com.ml.spam.utils.TextUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Crea un diccionario desde un archivo JSON con palabras organizadas por categoría.
 * Cada palabra se inicializa con frecuencia en cero.
 * Coordina la inicialización, actualización y exportación del diccionario.
 * Utiliza el ResourceHandler (inyectado para manejo de archivos en resources/static)
 * y el MessageProcessor (usado directamente para transformar listas crudas en objetos WordData).
 *
 */

public class SpamDictionaryService {

    private final SpamDictionary dictionary;
    private final ResourcesHandler resourcesHandler;

    // Atributo para registrar movimientos
    private List<String> movementLog = new ArrayList<>();

    public SpamDictionaryService() {
        this.resourcesHandler = new ResourcesHandler();
        this.dictionary = SpamDictionary.getInstance();
      }

    /**
     * Transforma palabras categorizadas sin frecuencias en WordData con frecuencias iniciales en cero.
     * Guarda el resultado en un nuevo archivo JSON para etapas posteriores.
     *
     * @param baseWordsPath Ruta al archivo JSON con palabras categorizadas (sin frecuencias).
     * @param outputPath Ruta donde se guardará el archivo JSON con frecuencias en cero.
     */
    public void transformBaseWordsToFrequenciesZero(String baseWordsPath, String outputPath) {
        try {
            JSONObject baseWordsJson = resourcesHandler.loadJson(baseWordsPath);
            JsonUtils.validateWordCategoryJsonStructure(baseWordsJson);

            // Transformar palabras en WordData con frecuencias iniciales
            Map<WordCategory, List<String>> categoryMap = JsonUtils.jsonToCategoryMap(baseWordsJson);
            System.out.println("[DEPURACION] * * * * * * * * * * * * * * * Desde transformBaseWordsToFrequenciesZero MUESTRO categoryMap que devuelve JsonUtils.jsonToCategoryMap :   \n"+ categoryMap+"\n");

            Map<WordCategory, Map<String, WordData>> categorizedWordsMap = new HashMap<>();

            categoryMap.forEach((category, words) -> {
                Map<String, WordData> wordsMap = words.stream()
                        .collect(Collectors.toMap(
                                word -> word,
                                word -> new WordData(word, 0, 0),
                                (existing, replacement) -> existing, // Resolver conflictos (no debería ocurrir)
                                HashMap::new // Forzar el uso de HashMap
                        ));
                categorizedWordsMap.put(category, wordsMap);
            });
            System.out.println("[DEPURACION] * * * * * * * * * * * * * * * Desde transformBaseWordsToFrequenciesZero MUESTRO categorizedWordMap luego del foreach :   \n"+ categorizedWordsMap+"\n");



            // Guardar en archivo JSON
            JSONObject outputJson = JsonUtils.categorizedWordsToJson(categorizedWordsMap);
            resourcesHandler.saveJson(outputJson, outputPath);

            // Cargar los datos en SpamDictionary
            categorizedWordsMap.forEach((category, wordsMap) ->
                    wordsMap.forEach((word, wordData) ->
                            dictionary.addWordWithFrequencies(category, word, wordData.getSpamFrequency(), wordData.getHamFrequency())
                    )
            );

            System.out.println("Palabras base transformadas y guardadas en: " + outputPath);
        } catch (Exception e) {
            throw new RuntimeException("Error al transformar palabras base: " + e.getMessage(), e);
        }
    }

    /**
     * Exporta el contenido actualizado de categorizedWords a un archivo JSON.
     *
     * @param relativePath Ruta donde se guardará el archivo actualizado.
     */

    public void exportUpdatedCategorizedWords(String relativePath) {
        try {
            // Obtener el estado actual de categorizedWords
            Map<WordCategory, Map<String, WordData>> categorizedWordsMap = dictionary.getCategorizedWords();

            // Convertir el mapa a JSON
            JSONObject outputJson = JsonUtils.categorizedWordsToJson(categorizedWordsMap);

            //Salida en consola solamente para pruebas
         //   System.out.println("[PRUEBA en Service.export] Se muestra categorizeWordsMap en formato json:"+outputJson);
         //   System.out.println("[PRUEBA en Service.export] Fin de muestra categorizeWordsMap en formato json.-------------");


            // Obtener una ruta única desde el ResourcesHandler
            String uniquePath = resourcesHandler.getUniqueFilePath(relativePath);

            // Guardar el JSON en la ruta única
            resourcesHandler.saveJson(outputJson, uniquePath);

            System.out.println("[INFO] Archivo actualizado exportado exitosamente a: " + uniquePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar categorizedWords actualizado: " + e.getMessage(), e);
        }
    }







    private Map<WordCategory, List<String>> cleanCategoryMap(Map<WordCategory, List<String>> categoryMap) {
        Map<WordCategory, List<String>> cleanedMap = new HashMap<>();

        categoryMap.forEach((category, words) -> {
            List<String> cleanedWords = words.stream()
                    .map(word -> {
                        if (category == WordCategory.RARE_SYMBOLS) {
                            // RARE_SYMBOLS no se alteran
                            return word;
                        } else {
                            // normalización inicial sin quitar acentos
                            return TextUtils.normalizeString(word);
                        }
                    })
                    .toList();
            cleanedMap.put(category, cleanedWords);
        });

        return cleanedMap;
    }


    //Inicializa solamente si las frecuencias están en cero
    ////Importante!!:No Carga los Pares Acentuados ya que no intervienen en esta etapa


    /**
     * Inicializa el diccionario solo si las frecuencias están en cero.
     */
    public void initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(String catWordsPath, String pairsFilePath, String lexemePath) {
        try {
            initializeCategorizedWordsZeroFrequencies(catWordsPath);
            initializeAccentPairs(pairsFilePath);
            initializeLexemes(lexemePath);

            System.out.println("\n[INFO] Diccionario inicializado correctamente.");
           // displayCategorizedWordsInDictionary();
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el diccionario: " + e.getMessage(), e);
        }
    }

    public void initializeDictionaryFromJson(String catWordsPath, String pairsFilePath, String lexemePath) {
        try {
            initializeCategorizedWordsFromJsonPath(catWordsPath);
            initializeAccentPairs(pairsFilePath);
            initializeLexemes(lexemePath);

            System.out.println("\n[INFO] Diccionario inicializado correctamente.");
            // displayCategorizedWordsInDictionary();
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el diccionario: " + e.getMessage(), e);
        }
    }

    private void initializeCategorizedWordsZeroFrequencies(String catWordsPath) {
        JSONObject jsonObject = resourcesHandler.loadJson(catWordsPath);
        JsonUtils.validateJsonFrequenciesZero(jsonObject);

        initializeCategorizedWordsFromJsonPath(catWordsPath);

        if (!dictionary.areFrequenciesZero()) {
            throw new IllegalStateException("Categorized Words contiene frecuencias no inicializadas a cero.");
        }

        System.out.println("[INFO] CATEGORIZED WORDS inicializados correctamente.");
    }

    // Inicializa accentPairs desde el JSON de acentos
    public void initializeAccentPairs(String accentPairsJsonPath) {
        // Cargar el JSON desde ResourcesHandler
        JSONObject accentJson = resourcesHandler.loadJson(accentPairsJsonPath);

        // Mapa para almacenar los pares acentuados
        Map<String, SpamDictionary.Pair> accentPairsMap = new HashMap<>();

        // Iterar sobre las claves del JSON
        for (String key : accentJson.keySet()) {
            // Obtener el valor asociado a la clave (el par)
            JSONObject pairObject = accentJson.getJSONObject(key);

            // Extraer los valores correspondientes
            String nonAccented = pairObject.getString("nonAccented");
            String categoryStr = pairObject.getString("category");

            // Validar y convertir la categoría
            WordCategory category;
            try {
                category = WordCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Categoría inválida en JSON: " + categoryStr);
            }

            // Crear el par y agregarlo al mapa
            SpamDictionary.Pair pair = new SpamDictionary.Pair(nonAccented, category);
            accentPairsMap.put(key, pair);
        }

        // Inicializar el diccionario con los pares procesados
        SpamDictionary.getInstance().initializeAccentPairs(accentPairsMap);
    }

    public void initializeLexemes(String lexemePath) {
        try {
            System.out.println("[INFO] Iniciando carga de lexemas desde: " + lexemePath);

            JSONObject lexemeJson = resourcesHandler.loadJson(lexemePath);
            JsonUtils.validateLexemeJsonStructure(lexemeJson);

            // Convertir el JSON a un mapa categorizado con la estructura correcta
            Map<CharSize, Map<String, Set<String>>> lexemesMap = JsonUtils.jsonToStructuredLexemeMap(lexemeJson);

            // [DEBUG] Imprimir el contenido del mapa de lexemas
           /*
            System.out.println("[DEBUG] Mapa de lexemas generados:");
            for (Map.Entry<CharSize, Map<String, Set<String>>> entry : lexemesMap.entrySet()) {
                System.out.println("Categoría: " + entry.getKey());
                Map<String, Set<String>> subCategories = entry.getValue();
                for (Map.Entry<String, Set<String>> subEntry : subCategories.entrySet()) {
                   System.out.println(" Subcategoría: " + subEntry.getKey());
                    for (String lexeme : subEntry.getValue()) {
                        System.out.println("   - " + lexeme);
                    }
                }
            }

            */

            // Transferir los lexemas al repositorio
            dictionary.initializeLexemeRepository(lexemesMap);

            System.out.println("[INFO] Lexemas inicializados correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar los lexemas: " + e.getMessage(), e);
        }
    }


    public void initializeCategorizedWordsFromJsonPath(String catWordsfilePath) {
        try {
            // Cargar el JSON desde el archivo
            JSONObject jsonObject = resourcesHandler.loadJson(catWordsfilePath);

            // Validar la estructura del JSON
            JsonUtils.validateWordCategoryJsonStructure(jsonObject);

            // Iterar sobre las categorías y actualizar el diccionario
            for (WordCategory category : WordCategory.values()) {
                // Usar el jsonKey en lugar del nombre del enum
                JSONObject categoryJson = jsonObject.optJSONObject(category.getJsonKey());
                if (categoryJson != null) {
                    // Ordenar las claves del JSON
                    List<String> sortedKeys = new ArrayList<>(categoryJson.keySet());
                    Collections.sort(sortedKeys);

                    for (String word : sortedKeys) {
                        JSONObject frequencies = categoryJson.getJSONObject(word);
                        int spamFrequency = frequencies.getInt("spamFrequency");
                        int hamFrequency = frequencies.getInt("hamFrequency");

                        // Agregar la palabra al diccionario
                        dictionary.addWordWithFrequencies(category, word, spamFrequency, hamFrequency);
                    }
                }
            }

            System.out.println("\n [INFO initialization] Diccionario inicializado correctamente desde el archivo JSON.\n");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el diccionario: " + e.getMessage(), e);
        }
    }


    /**
     * Carga los pares acentuados desde un archivo JSON.
     * @param pairsFilePath Ruta del archivo JSON de pares acentuados.
     * @return Lista de pares acentuados/no acentuados.
     */
    private List<SpamDictionary.Pair> loadAccentPairs(String pairsFilePath) {
        try {
            String jsonContent = resourcesHandler.loadResourceAsString(pairsFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    jsonContent,
                    new TypeReference<List<SpamDictionary.Pair>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar los pares acentuados: " + e.getMessage(), e);
        }
    }


    public void updateDictionaryFromCsvMessages(String csvMessagesFilePath) throws IOException {
        // 1. Obtener filas crudas del archivo CSV utilizando el ResourcesHandler
        List<String[]> rawRows = resourcesHandler.loadCsvFile(csvMessagesFilePath);

        // 2. Validar que las filas no estén vacías
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV no contiene datos válidos.");
        }

        // 3. Filtrar y validar filas utilizando TextUtils
        List<String[]> validRows = rawRows.stream()
                .filter(TextUtils::isRawRow) // Valida cada fila
                .toList();

        // 4. Verificar que haya al menos una fila válida
        if (validRows.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV no contiene filas válidas.");
        }

        // 5. Obtener recursos necesarios para el procesamiento
        Map<String, SpamDictionary.Pair> accentPairs = dictionary.getAccentPairs();
        Map<CharSize, Map<String, Set<String>>> lexemeRepository = dictionary.getLexemesRepository();

        // 6. Procesar las filas válidas para obtener listas de WordData
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(validRows, accentPairs, lexemeRepository);

        // 7. Actualizar el diccionario con los datos procesados
        updateDictionaryFromProcessedWordDataViejoMio(processedWordData);

        System.out.println("Diccionario actualizado correctamente con datos del archivo: " + csvMessagesFilePath);
    }

    //Los txt son mensajes sin label, por lo tanto se indicará si los mensajes son spam o ham.
    public void updateDictionaryFromTxt(String txtFilePath, String label) throws IOException {
        // Leer filas desde el archivo TXT
        List<String[]> rawRows = resourcesHandler.loadTxtFileAsRows(txtFilePath, label);

        // Validar filas y procesarlas
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("El archivo TXT no contiene datos válidos.");
        }

        // Procesar filas usando el MessageProcessor
        Map<String, SpamDictionary.Pair> accentPairs = dictionary.getAccentPairs();
        Map<CharSize, Map<String, Set<String>>> lexemeRepository = dictionary.getLexemesRepository();
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(rawRows, accentPairs, lexemeRepository);

        // Actualizar el diccionario con los datos procesados
        updateDictionaryFromProcessedWordDataViejoMio(processedWordData);
        //updateDictionaryFromProcessedWordData(processedWordData);
        System.out.println("Diccionario actualizado correctamente con datos del archivo TXT: " + txtFilePath);
    }


    ///////////////////////////////////////
    public void updateDictionaryFromProcessedWordDataViejo(List<List<WordData>> processedData) {
    // Estructura temporal para consolidar frecuencias
    Map<String, WordData> tempWordMap = new HashMap<>();

    // Consolidar frecuencias en el mapa temporal
    for (List<WordData> wordList : processedData) {
        for (WordData wordData : wordList) {
            String token = wordData.getWord().trim();

            if (token.isEmpty()) continue; // Ignorar palabras vacías

            tempWordMap.merge(token, wordData, (existing, newData) -> {
                existing.incrementSpamFrequency(newData.getSpamFrequency());
                existing.incrementHamFrequency(newData.getHamFrequency());
                return existing;
            });
        }
    }

    // Asignar palabras al diccionario después de consolidar
    for (Map.Entry<String, WordData> entry : tempWordMap.entrySet()) {
        String token = entry.getKey();
        WordData wordData = entry.getValue();

        // Verificar si la palabra ya existe en el diccionario
        if (dictionary.containsWord(token)) {
            updateExistingWordFrequencies(token, wordData);
        } else {
            // Determinar la categoría final basada en frecuencias totales
            WordCategory category = determineCategoryByFrequency(wordData);

            // Actualizar el diccionario con la palabra categorizada
            dictionary.addWordWithFrequencies(category, token, wordData.getSpamFrequency(), wordData.getHamFrequency());
        }
    }
}
////////////////////////////////////////////



/*
    public void updateDictionaryFromProcessedWordData(List<List<WordData>> processedData) {
        // Aplanar la estructura de las palabras
        List<WordData> flattenedWordData = processedData.stream()
                .flatMap(List::stream)
                .toList();

        // Convertir accentPairs a un mapa para búsquedas rápidas
        Map<String, SpamDictionary.Pair> accentPairMap = dictionary.getAccentPairs();

        // Procesar cada WordData
        for (WordData wordData : flattenedWordData) {
            String token = wordData.getWord().trim();

            // Validación básica: Palabra no vacía
            if (token.isEmpty()) {
                System.err.println("Palabra vacía encontrada. Se omite.");
                continue;
            }

            // Verificar si la palabra ya existe en el diccionario
            if (dictionary.containsWord(token)) {
                updateExistingWordFrequencies(token, wordData);
                continue;
            }

            // Manejo de palabras nuevas
            if (TextUtils.hasAccent(token)) {
                // Palabra con tilde
                SpamDictionary.Pair pair = accentPairMap.get(token);

                if (pair != null) {
                    // Si está en accentPairs, asignar a su categoría
                    dictionary.addWordWithFrequencies(
                            pair.category(), token, wordData.getSpamFrequency(), wordData.getHamFrequency()
                    );
                } else {
                    // No está en accentPairs: quitar tilde y buscar nuevamente
                    String tokenWithoutAccent = TextUtils.removeAccents(token);

                    if (dictionary.containsWord(tokenWithoutAccent)) {
                        // Consolidar frecuencias con la versión sin tilde
                        updateExistingWordFrequencies(tokenWithoutAccent, wordData);
                    } else {
                        // Asignar categoría basada en frecuencias
                        WordCategory category = determineCategoryByFrequency(wordData);
                        dictionary.addWordWithFrequencies(
                                category, tokenWithoutAccent, wordData.getSpamFrequency(), wordData.getHamFrequency()
                        );
                    }
                }
            } else {
                // Palabra sin tilde: determinar categoría directamente
                WordCategory category = determineCategoryByFrequency(wordData);
                dictionary.addWordWithFrequencies(
                        category, token, wordData.getSpamFrequency(), wordData.getHamFrequency()
                );
            }
        }
    }

 */

    /**
     * Determina la categoría de la palabra en función de las frecuencias de spam y ham:
     * - Si la frecuencia de spam es mayor que la de ham, se clasifica como SPAM_WORDS.
     * - Si la frecuencia de ham es mayor o igual a la de spam, se clasifica como UNASSIGNED_WORDS.
     *
     * @param wordData Objeto WordData que contiene las frecuencias de spam y ham.
     * @return La categoría correspondiente (SPAM_WORDS o UNASSIGNED_WORDS).
     */
    private WordCategory determineCategoryByFrequency(WordData wordData) {
        int spamFrequency = wordData.getSpamFrequency();
        int hamFrequency = wordData.getHamFrequency();

        if (spamFrequency > hamFrequency) {
            if (spamFrequency >= CategoryFrequencyThresholds.STRONG_SPAM_MIN.getValue()) {
                return WordCategory.STRONG_SPAM_WORD;
            } else if (spamFrequency >= CategoryFrequencyThresholds.MODERATE_SPAM_MIN.getValue() &&
                    spamFrequency <= CategoryFrequencyThresholds.MODERATE_SPAM_MAX.getValue()) {
                return WordCategory.MODERATE_SPAM_WORD;
            } else if (spamFrequency >= CategoryFrequencyThresholds.WEAK_SPAM_MIN.getValue() &&
                    spamFrequency <= CategoryFrequencyThresholds.WEAK_SPAM_MAX.getValue()) {
                return WordCategory.WEAK_SPAM_WORD;
            }
        }
        return WordCategory.UNASSIGNED_WORDS;
    }


    private void updateExistingWordFrequencies(String token, WordData wordData) {
        // Validación: ignorar tokens vacíos o nulos
        if (token == null || token.trim().isEmpty()) {
            System.out.printf("[DEBUG] Ignorando token vacío o nulo al actualizar frecuencias: '%s'%n", token);
            return;
        }
        // Buscar en todas las categorías y actualizar las frecuencias existentes
        for (WordCategory category : WordCategory.values()) {
            Map<String, WordData> categoryWords = dictionary.getCategory(category);
            if (categoryWords.containsKey(token)) {
                WordData existingWord = categoryWords.get(token);
                existingWord.incrementSpamFrequency(wordData.getSpamFrequency());
                existingWord.incrementHamFrequency(wordData.getHamFrequency());
                break;
            }
        }
    }


    //----------------------Metod auxiliar para update
   // public void Viejo______updateDictionaryFromProcessedWordData(List<List<WordData>> processedData) {

        // Aplanar la estructura
     //   List<WordData> flattenedWordData = processedData.stream()
     //           .flatMap(List::stream)
     //           .toList();

        // Actualizar el diccionario
       // for (WordData wordData : flattenedWordData) {
      //      WordCategory category = determineCategory(wordData.getWord()); // Determinar categoría

       //     dictionary.getCategory(category).merge(
      //              wordData.getWord(),
       //             wordData,
        //            (existing, newData) -> {
        //                existing.incrementSpamFrequency(newData.getSpamFrequency());
        //                existing.incrementHamFrequency(newData.getHamFrequency());
         //               return existing;
                 //   }
       //     );
       // }
    //}





    /*
             // ****************************************************** //
             //  * * * * Por ahora no utilizo ProcessedMessages * * * *
             // ****************************************************** //
    public void updateDictionaryFromProcessedMessages(List<ProcessedMessage> messages) {
        for (ProcessedMessage message : messages) {
            for (String word : message.getTokens()) {
                boolean found = false;

                // Recorremos las categorías para ver si la palabra pertenece a alguna
                for (WordCategory category : WordCategory.values()) {
                    Map<String, WordData> categoryWords = dictionary.get(category);
                    if (categoryWords != null && categoryWords.containsKey(word)) {
                        WordData wordData = categoryWords.get(word);

                        // Actualizamos la frecuencia según el label (spam/ham)
                        if ("ham".equals(message.getLabel())) {
                            wordData.incrementHamCount(message.getWordFrequency().get(word));
                        } else if ("spam".equals(message.getLabel())) {
                            wordData.incrementSpamCount(message.getWordFrequency().get(word));
                        }
                        found = true;
                        break;
                    }
                }

                // Si la palabra no está en ninguna categoría, la agregamos a UNASSIGNED_WORDS
                if (!found) {
                    Map<String, WordData> unassignedWords = dictionary.get(WordCategory.UNASSIGNED_WORDS);
                    if (unassignedWords == null) {
                        unassignedWords = new HashMap<>();
                        dictionary.put(WordCategory.UNASSIGNED_WORDS, unassignedWords);
                    }

                    // Creamos una nueva entrada de WordData si no existe
                    WordData wordData = unassignedWords.computeIfAbsent(word, k -> new WordData());

                    // Aquí no contamos 'ham' ni 'spam', solo incrementamos 'unassignedCount'
                    wordData.incrementUnassignedCount(message.getWordFrequency().get(word));
                }
            }
        }
    }
*/


    /**
     * Exporta el contenido del diccionario a un archivo JSON en el sistema.
     * @param filePath Ruta absoluta o relativa donde se guardará el JSON.
     */
    public void exportDictionaryToJson(String filePath) {
        try {
            // Obtener el diccionario categorizado
            Map<WordCategory, Map<String, WordData>> categorizedDictionary = dictionary.getAllCategorizedWords();

            // Convertir el diccionario a JSON usando JsonUtils
            JSONObject jsonObject = JsonUtils.categorizedWordsToJson(categorizedDictionary);

            // Guardar el JSON utilizando el handler de recursos
            resourcesHandler.saveJson(jsonObject, filePath);

            System.out.println("Diccionario exportado a: " + filePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar el diccionario: " + e.getMessage(), e);
        }
    }

    /**
     * Muestra el contenido actual del diccionario en la consola.
     */
    public void displayCategorizedWordsInDictionary() {
        System.out.println("\n========= Contenido de CATEGORIZED WORDS en DICTIONARY =========\n");
        for (WordCategory category : WordCategory.values()) {
            System.out.println("Categoría: " + category);
            dictionary.getCategory(category).forEach((word, wordData) ->
                    System.out.println("  " + word + " -> " + wordData)
            );
        }
    }

    public void displayLexemeRepository() {
        System.out.println("\n========= Contenido del Lexeme Repository =========\n");
        Map<CharSize, Map<String, Set<String>>> lexemeRepository = dictionary.getLexemesRepository();

        if (lexemeRepository == null || lexemeRepository.isEmpty()) {
            System.out.println("El repositorio de lexemas está vacío o no inicializado.");
            return;
        }

        lexemeRepository.forEach((category, subCategories) -> {
            System.out.println("Categoría: " + category);

            if (subCategories == null || subCategories.isEmpty()) {
                System.out.println("  (sin subcategorías o lexemas)");
            } else {
                subCategories.forEach((subCategory, lexemes) -> {
                    System.out.println("  Subcategoría: " + subCategory);

                    if (lexemes == null || lexemes.isEmpty()) {
                        System.out.println("    (sin lexemas)");
                    } else {
                        lexemes.forEach(lexeme -> System.out.println("    - " + lexeme));
                    }
                });
            }
        });
    }

    /**
     * Muestra el contenido de un archivo JSON desde resources en la consola.
     * @param resourcePath Ruta relativa del archivo JSON en los recursos.
     */
    public void displayJsonFileDictionary(String resourcePath) {
        try {
            // Leer el contenido del archivo desde recursos
            String jsonContent = resourcesHandler.loadResourceAsString(resourcePath);

            // Mostrar el contenido en la consola
            System.out.println("=== Diccionario Persistido en JSON ===");
            System.out.println(jsonContent);
        } catch (Exception e) {
            System.err.println("Error al leer el archivo JSON desde recursos: " + e.getMessage());
        }
    }

    public void displayAccentPairsInDictionary() {
        System.out.println("=== Mostrar accentPairs en SpamDictionary ===");
        Map<String, SpamDictionary.Pair> accentPairs = SpamDictionary.getInstance().getAccentPairs();

        if (accentPairs.isEmpty()) {
            System.out.println("accentPairs está vacío.");
        } else {
            accentPairs.forEach((key, pair) -> {
                System.out.println("Palabra acentuada: " + key +
                        ", No acentuada: " + pair.nonAccented() +
                        ", Categoría: " + pair.category());
            });
        }
        System.out.println("=============================================\n");
    }


    public void displayFullReport() {
        System.out.println("\n=== Generating Full Dictionary Report ===");
        DictionarySummaryReport.displayFullReport(this);
        System.out.println("=== Report Generation Complete ===");
    }



    public SpamDictionary getDictionary() {
        return this.dictionary;
    }

    public Map<CharSize, Map<String, Set<String>>> getLexemesRepository() {
        return dictionary.getLexemesRepository();
    }

    public Map<String, SpamDictionary.Pair> getAccentPairs() {
        return dictionary.getAccentPairs();
    }


    /**----------------------------------------------------------
     * ------------- REASIGNACIÓN DE PALABRAS --------------------
     * ---------------------------------------------------------*/
    public void reassignWordsFromUpdatedJson() {
        String word = null;
        WordCategory currentCategory = null;

        try {
            // Obtener las palabras categorizadas del diccionario
            Map<WordCategory, Map<String, WordData>> categoryMapMap = dictionary.getAllCategorizedWords();

            if (categoryMapMap == null || categoryMapMap.isEmpty()) {
                System.err.println("[ERROR] El diccionario categorizado está vacío o no se inicializó correctamente.");
                return;
            }

            // Iterar por cada categoría y palabra
            for (Map.Entry<WordCategory, Map<String, WordData>> categoryEntry : categoryMapMap.entrySet()) {
                currentCategory = categoryEntry.getKey();

                // Saltar categorías que no deben modificarse
                if (currentCategory == WordCategory.RARE_SYMBOLS || currentCategory == WordCategory.STOP_WORDS) {
                    System.out.printf("[INFO protection!! ] La categoría '%s' está protegida y no se modificará.%n", currentCategory.getJsonKey());
                    continue;
                }

                Map<String, WordData> words = categoryEntry.getValue();

                if (words == null || words.isEmpty()) {
                    System.out.printf("[WARNING] La categoría '%s' no contiene palabras.%n", currentCategory.getJsonKey());
                    continue;
                }

                for (Map.Entry<String, WordData> wordEntry : new HashMap<>(words).entrySet()) {
                    word = wordEntry.getKey();
                    WordData wordData = wordEntry.getValue();

                    // Validar palabra y frecuencias
                    if (word == null || wordData == null || wordData.getSpamFrequency() < 0 || wordData.getHamFrequency() < 0) {
                        System.err.printf("[ERROR] Palabra o frecuencias inválidas en '%s'. Ignorando.%n", word);
                        continue;
                    }

                    // Eliminar palabras con frecuencias cero
                    if (wordData.getSpamFrequency() == 0 && wordData.getHamFrequency() == 0) {
                        words.remove(word);
                        System.out.printf("[INFO] Palabra '%s' eliminada por frecuencias cero.%n", word);
                        continue;
                    }

                    // Determinar nueva categoría
                    WordCategory newCategory = determineCategoryByDifference(wordData.getSpamFrequency(), wordData.getHamFrequency());

                    // Reasignar si la categoría cambia
                    if (!currentCategory.equals(newCategory)) {
                        moveWordToCategory(word, wordData, currentCategory, newCategory, categoryMapMap);
                    }
                }
            }

            System.out.println("[INFO] Reasignación de palabras completada.\n");
            System.out.printf("[INFO] Total de palabras movidas: %d%n", movementLog.size());
        } catch (Exception e) {
            System.err.printf("[ERROR] Falló al procesar palabra '%s' en categoría '%s': %s%n",
                    word, currentCategory != null ? currentCategory.getJsonKey() : "desconocida", e.getMessage());
            e.printStackTrace();
        }
    }

    private WordCategory determineCategoryByDifference(int spamFrequency, int hamFrequency) {
        int difference = spamFrequency - hamFrequency;

        if (difference >= CategoryFrequencyThresholds.STRONG_SPAM_MIN.getValue()) {
            return WordCategory.STRONG_SPAM_WORD;
        } else if (difference >= CategoryFrequencyThresholds.MODERATE_SPAM_MIN.getValue() &&
                difference <= CategoryFrequencyThresholds.MODERATE_SPAM_MAX.getValue()) {
            return WordCategory.MODERATE_SPAM_WORD;
        } else if (difference >= CategoryFrequencyThresholds.WEAK_SPAM_MIN.getValue() &&
                difference <= CategoryFrequencyThresholds.WEAK_SPAM_MAX.getValue()) {
            return WordCategory.WEAK_SPAM_WORD;
        } else if (hamFrequency > spamFrequency) {
            return WordCategory.HAM_INDICATORS;
        } else {
            return WordCategory.UNASSIGNED_WORDS;
        }
    }

    private void moveWordToCategory(String word, WordData wordData,
                                    WordCategory currentCategory, WordCategory newCategory,
                                    Map<WordCategory, Map<String, WordData>> categoryMapMap) {
        if (!categoryMapMap.containsKey(currentCategory)) {
            System.err.printf("[ERROR] La categoría actual '%s' no existe para la palabra '%s'.%n",
                    currentCategory.getJsonKey(), word);
            return;
        }

        if (!categoryMapMap.containsKey(newCategory)) {
            categoryMapMap.put(newCategory, new HashMap<>()); // Inicializa la nueva categoría si no existe
        }

        // Eliminar de la categoría actual
        categoryMapMap.get(currentCategory).remove(word);

        // Agregar a la nueva categoría
        categoryMapMap.get(newCategory).put(word, wordData);

        // Registrar el movimiento
        String logEntry = String.format("La palabra '%s' en categoría '%s' se movió a '%s'.",
                word, currentCategory.getJsonKey(), newCategory.getJsonKey());
        movementLog.add(logEntry);

        System.out.println("[INFO] " + logEntry);
    }


    /*
    ---------------------------------------PRUEBAS---------------------------------------
    ------------------------------------------------------------------------------------
     */

    /**
     * Crea un diccionario desde un archivo JSON con palabras organizadas por categoría.
     * Cada palabra se inicializa con frecuencia en cero.
     *
     * @param resourcePath Ruta relativa del archivo JSON en los recursos.
     */
    public void createCategorizedWordsFromJson(String resourcePath) {
        try {
            // Leer JSON desde el handler
            JSONObject jsonObject = resourcesHandler.loadJson(resourcePath);

            // Validar la estructura del JSON. Deben estar las Categorías.
            JsonUtils.validateWordCategoryJsonStructure(jsonObject);

            // Transformar el JSON en un mapa
            Map<WordCategory, List<String>> categoryMap = JsonUtils.jsonToCategoryMap(jsonObject);

            // Limpieza y normalización de palabras en todas las categorías
            Map<WordCategory, List<String>> cleanedCategoryMap = cleanCategoryMap(categoryMap);

            // Inicializar el diccionario
            dictionary.clearDictionary();

            // Procesar cada categoría y sus palabras limpias
            cleanedCategoryMap.forEach(dictionary::initializeWordsWithZeroFrequency);

            System.out.println("Diccionario creado desde palabras en el archivo: " + resourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el diccionario desde palabras: " + e.getMessage(), e);
        }
    }


    /*
    public void updateDictionaryFromProcessedWordData(List<List<WordData>> processedData) {
        // Estructura temporal para consolidar frecuencias
        Map<String, WordData> tempWordMap = new HashMap<>();

        // Consolidar frecuencias en el mapa temporal
        for (List<WordData> wordList : processedData) {
            for (WordData wordData : wordList) {
                String token = wordData.getWord().trim();

                if (token.isEmpty()) continue; // Ignorar palabras vacías

                tempWordMap.merge(token, wordData, (existing, newData) -> {
                    existing.incrementSpamFrequency(newData.getSpamFrequency());
                    existing.incrementHamFrequency(newData.getHamFrequency());
                    return existing;
                });
            }
        }

        // Asignar palabras al diccionario después de consolidar
        for (Map.Entry<String, WordData> entry : tempWordMap.entrySet()) {
            String token = entry.getKey();
            WordData wordData = entry.getValue();

            // Verificar si la palabra ya existe en el diccionario
            if (dictionary.containsWord(token)) {
                // Buscar y eliminar la palabra de su categoría actual
                for (WordCategory category : WordCategory.values()) {
                    Map<String, WordData> categoryWords = dictionary.getCategory(category);
                    if (categoryWords.containsKey(token)) {
                        categoryWords.remove(token);
                        break; // Salir tras encontrar la palabra
                    }
                }

                // Actualizar las frecuencias en el diccionario
                updateExistingWordFrequencies(token, wordData);
            } else {
                // Determinar la categoría final basada en frecuencias totales
                WordCategory category = determineCategoryByFrequency(wordData);

                // Actualizar el diccionario con la palabra categorizada
                dictionary.addWordWithFrequencies(category, token, wordData.getSpamFrequency(), wordData.getHamFrequency());
            }
        }
    }
*/




    ///////////////////////////Actualizacion del diccionario: update Main////////////////////////////////////////////////
    public void updateDictionaryFromProcessedWordData(List<List<WordData>> processedData) {
        // Estructura temporal para consolidar frecuencias
        Map<String, WordData> tempWordMap = new HashMap<>();

        // Consolidar frecuencias en el mapa temporal
        consolidateFrequencies(processedData, tempWordMap);

        // Iterar por cada palabra consolidada
        for (Map.Entry<String, WordData> entry : tempWordMap.entrySet()) {
            String token = entry.getKey();
            WordData wordData = entry.getValue();

            // Validación: ignorar tokens vacíos o nulos
            if (token == null || token.trim().isEmpty()) {
                System.out.printf("[DEBUG] Ignorando token vacío o nulo: '%s'%n", token);
                continue;
            }
            // Verificar si la palabra pertenece a categorías protegidas
            if (isProtectedCategory(token)) {
                System.out.printf("[INFO] Palabra '%s' en categoría protegida no será modificada.%n", token);
                continue;
            }

            // Actualizar o reasignar palabra
            updateOrReassignWord(token, wordData);
        }
    }

    private void consolidateFrequencies(List<List<WordData>> processedData, Map<String, WordData> tempWordMap) {
        for (List<WordData> wordList : processedData) {
            for (WordData wordData : wordList) {
                String token = wordData.getWord().trim();

                // Validación: ignorar tokens vacíos o nulos
                if (token == null || token.trim().isEmpty()) {
                    System.out.printf("[DEBUG] Ignorando token vacío o nulo durante consolidación: '%s'%n", token);
                    continue;
                }

                // Consolidar frecuencias
                tempWordMap.merge(token, wordData, (existing, newData) -> {
                    existing.incrementSpamFrequency(newData.getSpamFrequency());
                    existing.incrementHamFrequency(newData.getHamFrequency());
                    return existing;
                });
            }
        }
    }
    private boolean isProtectedCategory(String token) {
        for (WordCategory category : List.of(WordCategory.RARE_SYMBOLS, WordCategory.STOP_WORDS)) {
            if (dictionary.getCategory(category).containsKey(token)) {
                return true;
            }
        }
        return false;
    }
    private void updateOrReassignWord(String token, WordData wordData) {
        // Validación: ignorar tokens vacíos o nulos
        if (token == null || token.trim().isEmpty()) {
            System.out.printf("[DEBUG] Ignorando token vacío o nulo al actualizar o reasignar: '%s'%n", token);
            return;
        }

        WordCategory currentCategory = null;

        // Buscar la categoría actual de la palabra
        for (WordCategory category : WordCategory.values()) {
            if (dictionary.getCategory(category).containsKey(token)) {
                currentCategory = category;
                break;
            }
        }

        // Determinar la categoría final
        WordCategory newCategory = determineCategoryByFrequency(wordData);

        if (currentCategory != null) {
            if (!currentCategory.equals(newCategory)) {
                // Mover la palabra si la categoría cambia
                moveWordToCategory(token, wordData, currentCategory, newCategory, dictionary.getCategorizedWords());
            } else {
                // Actualizar frecuencias en la misma categoría
                updateExistingWordFrequencies(token, wordData);
            }
        } else {
            // Palabra nueva: agregar directamente
            dictionary.addWordWithFrequencies(newCategory, token, wordData.getSpamFrequency(), wordData.getHamFrequency());
        }
    }


    //////////////////////////////////////////////////////////////////////////










    ///////-----///////////
    public void updateDictionaryFromProcessedWordDataViejoMio(List<List<WordData>> processedData) {

        // Estructura temporal para consolidar frecuencias
        Map<String, WordData> tempWordMap = new HashMap<>();

        // Consolidar frecuencias en el mapa temporal
        for (List<WordData> wordList : processedData) {
            for (WordData wordData : wordList) {
                String token = wordData.getWord().trim();

                if (token.isEmpty()) continue; // Ignorar palabras vacías

                tempWordMap.merge(token, wordData, (existing, newData) -> {
                    existing.incrementSpamFrequency(newData.getSpamFrequency());
                    existing.incrementHamFrequency(newData.getHamFrequency());
                    return existing;
                });
            }
        }

        // Asignar palabras al diccionario después de consolidar
        for (Map.Entry<String, WordData> entry : tempWordMap.entrySet()) {
            String token = entry.getKey();
            WordData wordData = entry.getValue();

            // Validación: ignorar tokens vacíos o nulos
            if (token == null || token.trim().isEmpty()) {
                System.out.printf("[DEBUG] Ignorando token vacío o nulo: '%s'%n", token);
                continue;
            }
            // Verificar si la palabra ya existe en el diccionario
            if (dictionary.containsWord(token)) {
                updateExistingWordFrequenciesMio(token, wordData);
            } else {
                // Determinar la categoría final basada en frecuencias totales
                WordCategory category = determineCategoryByFrequency(wordData);

                // Actualizar el diccionario con la palabra categorizada
                dictionary.addWordWithFrequencies(category, token, wordData.getSpamFrequency(), wordData.getHamFrequency());
            }
        }//palabra asignada


    }

    private void updateExistingWordFrequenciesMio(String token, WordData wordData) {
        // Consolidar frecuencias y revisar categorías
        for (WordCategory category : WordCategory.values()) {
            Map<String, WordData> categoryWords = dictionary.getCategory(category);

            // Si la palabra ya está en una categoría
            if (categoryWords.containsKey(token)) {
                WordData existingWord = categoryWords.get(token);

                // Actualizar frecuencias
                existingWord.incrementSpamFrequency(wordData.getSpamFrequency());
                existingWord.incrementHamFrequency(wordData.getHamFrequency());

                // Si la categoría es protegida, no se mueve la palabra
                if (category == WordCategory.RARE_SYMBOLS || category == WordCategory.STOP_WORDS) {
                    System.out.printf("[INFO] La palabra '%s' permanece en la categoría protegida '%s'.%n", token, category.getJsonKey());
                    return; // Salir del flujo para palabras protegidas
                }

                // Recalcular la categoría basada en las nuevas frecuencias
                WordCategory updatedCategory = determineCategoryByDifference(
                        existingWord.getSpamFrequency(),
                        existingWord.getHamFrequency()
                );

                // Si la categoría cambia, reasignar la palabra
                if (updatedCategory != category) {
                    categoryWords.remove(token); // Eliminar de la categoría actual
                    dictionary.addWordWithFrequencies(updatedCategory, token,
                            existingWord.getSpamFrequency(), existingWord.getHamFrequency());
                    System.out.printf("[INFO] La palabra '%s' se movió de '%s' a '%s'.%n", token, category.getJsonKey(), updatedCategory.getJsonKey());
                }
                return; // Salir del flujo tras procesar la palabra
            }
        }

        // Si la palabra no estaba en ninguna categoría, calcular su categoría inicial
        WordCategory newCategory = determineCategoryByDifference(
                wordData.getSpamFrequency(), wordData.getHamFrequency()
        );
        dictionary.addWordWithFrequencies(newCategory, token,
                wordData.getSpamFrequency(), wordData.getHamFrequency());
        System.out.printf("[INFO] La palabra '%s' se agregó a la categoría '%s'.%n", token, newCategory.getJsonKey());
    }

    ////////////////-----------///////////////////




    /////////////////////// Exportar categorized Words sin las frecuencias ///////////////////////////////
    /**
     * Exporta las palabras categorizadas sin frecuencias, delegando el manejo de archivos al ResourcesHandler.
     * Omite palabras con ambas frecuencias en cero y genera un reporte de estas.
     *
     * @param outputPathBase Ruta base donde se guardará el archivo JSON sin frecuencias.
     * @param reportPathBase Ruta base donde se guardará el reporte de palabras omitidas.
     */
    public void exportCategorizedWordsWithoutFrequencies(String outputPathBase, String reportPathBase) {
        try {
            // Transformar las palabras categorizadas a una estructura sin frecuencias
            Map<String, List<String>> baseWords = transformCategorizedWordsToBaseWords();

            // Generar el reporte de palabras omitidas
            List<String> omittedWordsReport = generateOmittedWordsReport();

            // Delegar la exportación y generación de rutas únicas al ResourcesHandler
            resourcesHandler.saveCategorizedWordsOnlyAndReport(baseWords, omittedWordsReport, outputPathBase, reportPathBase);

            System.out.println("[INFO] Exportación y reporte delegados exitosamente al ResourcesHandler.");
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar palabras sin frecuencias: " + e.getMessage(), e);
        }
    }

    /**
     * Transforma el diccionario categorizado en una estructura sin frecuencias,
     * omitiendo palabras con ambas frecuencias en cero.
     *
     * @return Mapa categorizado con listas de palabras sin frecuencias.
     */
    private Map<String, List<String>> transformCategorizedWordsToBaseWords() {
        Map<WordCategory, Map<String, WordData>> categorizedWords = dictionary.getCategorizedWords();
        Map<String, List<String>> baseWords = new HashMap<>();

        for (Map.Entry<WordCategory, Map<String, WordData>> entry : categorizedWords.entrySet()) {
            WordCategory category = entry.getKey();
            Map<String, WordData> words = entry.getValue();

            // Filtrar palabras con frecuencias mayores a cero
            List<String> filteredWords = words.values().stream()
                    .filter(wordData -> wordData.getSpamFrequency() > 0 || wordData.getHamFrequency() > 0)
                    .map(WordData::getWord)
                    .toList();

            if (!filteredWords.isEmpty()) {
                baseWords.put(category.getJsonKey(), filteredWords);
            }
        }

        return baseWords;
    }

    /**
     * Genera un reporte de palabras omitidas (con ambas frecuencias en cero).
     *
     * @return Lista de líneas para el reporte.
     */
    private List<String> generateOmittedWordsReport() {
        Map<WordCategory, Map<String, WordData>> categorizedWords = dictionary.getCategorizedWords();
        List<String> omittedWordsReport = new ArrayList<>();

        for (Map.Entry<WordCategory, Map<String, WordData>> entry : categorizedWords.entrySet()) {
            WordCategory category = entry.getKey();
            Map<String, WordData> words = entry.getValue();

            words.values().stream()
                    .filter(wordData -> wordData.getSpamFrequency() == 0 && wordData.getHamFrequency() == 0)
                    .forEach(wordData -> omittedWordsReport.add(
                            String.format("Palabra: '%s', Categoría: '%s'", wordData.getWord(), category.getJsonKey())));
        }

        return omittedWordsReport;
    }








}//end
