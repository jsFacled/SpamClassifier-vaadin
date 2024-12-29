package com.ml.spam.dictionary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.dictionary.models.LexemeRepositoryCategories;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
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
            displayCategorizedWordsInDictionary();
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

    private void initializeAccentPairs(String pairsFilePath) {
        List<SpamDictionary.Pair> accentPairs = loadAccentPairs(pairsFilePath);

        if (accentPairs == null || accentPairs.isEmpty()) {
            throw new IllegalStateException("No se pudieron cargar los pares acentuados.");
        }

        Map<String, SpamDictionary.Pair> accentPairsMap = accentPairs.stream()
                .collect(Collectors.toMap(SpamDictionary.Pair::accented, pair -> pair));

        dictionary.initializeAccentPairs(accentPairsMap);

        System.out.println("[INFO] ACCENT PAIRS inicializados correctamente.");
    }

    public void initializeLexemes(String lexemePath) {
        try {
            System.out.println("[INFO] Iniciando carga de lexemas desde: " + lexemePath);

            JSONObject lexemeJson = resourcesHandler.loadJson(lexemePath);
            JsonUtils.validateLexemeJsonStructure(lexemeJson);

            // Convertir el JSON a un mapa categorizado
            Map<LexemeRepositoryCategories, Set<String>> lexemesMap = JsonUtils.jsonToLexemeMap(lexemeJson);

            // Transferir los lexemas al repositorio
            dictionary.initializeLexemes(lexemesMap);

            System.out.println("[INFO] Lexemas inicializados correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar los lexemas: " + e.getMessage(), e);
        }
    }


    public JSONObject loadJson(String filePath){
        // Cargar el JSON desde el archivo
        return resourcesHandler.loadJson(filePath);
    }
    public void initializeCategorizedWordsFromJsonPath(String filePath) {
        try {
            // Cargar el JSON desde el archivo
            JSONObject jsonObject = resourcesHandler.loadJson(filePath);

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

            System.out.println("\n [INFO] Diccionario inicializado correctamente desde el archivo JSON.");
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


    public void updateDictionary(String csvFilePath) throws IOException {
        // 1. Obtener filas crudas del archivo CSV utilizando el ResourcesHandler
        List<String[]> rawRows = resourcesHandler.loadCsvFile(csvFilePath);

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
        Map<LexemeRepositoryCategories, Set<String>> lexemeRepository = dictionary.getLexemesRepository();

        // 6. Procesar las filas válidas para obtener listas de WordData
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(validRows, accentPairs, lexemeRepository);

        // 7. Actualizar el diccionario con los datos procesados
        updateDictionaryFromProcessedWordData(processedWordData);

        System.out.println("Diccionario actualizado correctamente con datos del archivo: " + csvFilePath);
    }


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


    /**
     * Determina la categoría de la palabra en función de las frecuencias de spam y ham:
     * - Si la frecuencia de spam es mayor que la de ham, se clasifica como SPAM_WORDS.
     * - Si la frecuencia de ham es mayor o igual a la de spam, se clasifica como UNASSIGNED_WORDS.
     *
     * @param wordData Objeto WordData que contiene las frecuencias de spam y ham.
     * @return La categoría correspondiente (SPAM_WORDS o UNASSIGNED_WORDS).
     */
    private WordCategory determineCategoryByFrequency(WordData wordData) {
        if (wordData.getSpamFrequency() > wordData.getHamFrequency()) {
            return WordCategory.SPAM_WORDS;
        } else {
            return WordCategory.UNASSIGNED_WORDS;
        }
    }

    private void updateExistingWordFrequencies(String token, WordData wordData) {
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


    public void displayFullReport() {
        System.out.println("\n=== Generating Full Dictionary Report ===");
        DictionarySummaryReport.displayFullReport(this);
        System.out.println("=== Report Generation Complete ===");
    }

    // Verifica si la palabra pertenece a la categoría SPAM_WORDS
    public boolean isSpamWord(String word) {
        return dictionary.getCategory(WordCategory.SPAM_WORDS).containsKey(word.toLowerCase());
    }

    // Verifica si la palabra pertenece a la categoría STOP_WORDS
    public boolean isStopWord(String word) {
        return dictionary.getCategory(WordCategory.STOP_WORDS).containsKey(word.toLowerCase());
    }

    // Determina si la palabra pertenece a la categoría RARE_SYMBOLS
    public boolean containsRareSymbols(String word) {
        Map<String, WordData> rareSymbols = dictionary.getCategory(WordCategory.RARE_SYMBOLS);
        for (char c : word.toCharArray()) {
            if (rareSymbols.containsKey(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }
    public SpamDictionary getDictionary() {
        return this.dictionary;
    }

    public Map<LexemeRepositoryCategories, Set<String>> getLexemesRepository() {
        return dictionary.getLexemesRepository();
    }

    public Map<String, SpamDictionary.Pair> getAccentPairs() {
        return dictionary.getAccentPairs();
    }



}
