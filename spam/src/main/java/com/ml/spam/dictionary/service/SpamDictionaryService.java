package com.ml.spam.dictionary.service;

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
    public void initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(String catWordsPath, String lexemePath) {
        try {
            initializeCategorizedWordsZeroFrequencies(catWordsPath);

            initializeLexemes(lexemePath);

            System.out.println("\n[INFO] Diccionario inicializado correctamente.");
           // displayCategorizedWordsInDictionary();
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el diccionario: " + e.getMessage(), e);
        }
    }

    public void initializeDictionaryFromJson(String catWordsPath, String lexemePath, String metadataPath) {
        try {
            initializeCategorizedWordsFromJsonPath(catWordsPath);
            initializeLexemes(lexemePath);
            initializeMetadata(metadataPath);

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

    public void initializeMetadata(String metadataPath) {
        try {
            System.out.println("[INFO] Iniciando carga de metadata desde: " + metadataPath);

            JSONObject metadataJson = resourcesHandler.loadJson(metadataPath);
            JsonUtils.validateMetadataJsonStructure(metadataJson);

            // Convertir el JSON a un objeto SpamDictionaryMetadata
            SpamDictionaryMetadata metadata = JsonUtils.jsonToSpamDictionaryMetadata(metadataJson);

            // Cargar el objeto en el diccionario
            dictionary.setMetadata(metadata);

            System.out.println("[INFO] Metadata inicializado correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar los metadatos: " + e.getMessage(), e);
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
        Map<CharSize, Map<String, Set<String>>> lexemeRepository = dictionary.getLexemesRepository();

        // 6. Procesar las filas válidas para obtener listas de WordData
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(validRows, lexemeRepository);

        // 7. Contar spam y ham antes de actualizar el diccionario y sumarlos al metadata
        int spamCount = 0;
        int hamCount = 0;
        for (String[] row : validRows) {
            if (row.length > 1) {
                String label = row[1].trim().toLowerCase();
                if (label.equals("spam")) spamCount++;
                else if (label.equals("ham")) hamCount++;
            }
        }
        SpamDictionary.getInstance().getMetadata().addSpam(spamCount);
        SpamDictionary.getInstance().getMetadata().addHam(hamCount);

        // También sumar la cantidad total de instancias
        SpamDictionary.getInstance().getMetadata().addInstances(validRows.size());

        // 8. Actualizar el diccionario con los datos procesados
        updateDictionaryFromProcessedWordData(processedWordData);

        System.out.println("Diccionario actualizado correctamente con datos del archivo: " + csvMessagesFilePath);
    }

    //Los txt son mensajes sin label, por lo tanto se indicará si los mensajes son spam o ham.
    public void updateDictionaryFromTxt(String txtFilePath, String label) throws Exception {
        List<String[]> labeledMessages = resourcesHandler.extractMessagesAndAddLabelFromTxt(txtFilePath, label);

        if (labeledMessages == null || labeledMessages.isEmpty()) {
            throw new IllegalArgumentException("El archivo TXT no contiene datos válidos.");
        }


        // Obtener repositorio de lexemas
        Map<CharSize, Map<String, Set<String>>> lexemeRepository = dictionary.getLexemesRepository();

        // Procesar a listas de WordData
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(labeledMessages, lexemeRepository);

        // Contabilizar en metadatos según etiqueta
        if (label.equalsIgnoreCase("spam")) {
            System.out.println("[ %%%%%%%%%%%%% DEBUG mensajes triplecuot ] : labeledMessages.size() = " + labeledMessages.size());
            System.out.println("[%%%%%%%%%%%%% DEBUG mensajes triplecuot ]: processedWordData.size() = " + processedWordData.size());
            SpamDictionary.getInstance().getMetadata().addSpam(processedWordData.size());
        } else if (label.equalsIgnoreCase("ham")) {
            SpamDictionary.getInstance().getMetadata().addHam(processedWordData.size());
        }


        // Actualizar el diccionario
        updateDictionaryFromProcessedWordData(processedWordData);

        System.out.println("Diccionario actualizado correctamente con datos del archivo TXT: " + txtFilePath);
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

    public Map<WordCategory, Map<String, WordData>> getCategorizedWords() { return dictionary.getCategorizedWords();
    }



    /**----------------------------------------------------------
     * ------------- REASIGNACIÓN DE PALABRAS --------------------
     * ---------------------------------------------------------*/


    public void reassignWordsFromUpdatedJson() {
        String word = null;
        WordCategory currentCategory = null;

        try {
            Map<WordCategory, Map<String, WordData>> categoryMapMap = dictionary.getAllCategorizedWords();

            if (categoryMapMap == null || categoryMapMap.isEmpty()) {
                System.err.println("[ERROR] El diccionario categorizado está vacío o no se inicializó correctamente.");
                return;
            }

            for (Map.Entry<WordCategory, Map<String, WordData>> categoryEntry : categoryMapMap.entrySet()) {
                currentCategory = categoryEntry.getKey();

                if (currentCategory == WordCategory.RARE_SYMBOLS || currentCategory == WordCategory.STOP_WORDS) {
                    System.out.printf("[INFO] La categoría '%s' está protegida y no se modificará.%n", currentCategory.getJsonKey());
                    continue;
                }

                Map<String, WordData> words = categoryEntry.getValue();
                if (words == null || words.isEmpty()) continue;

                for (Map.Entry<String, WordData> wordEntry : new HashMap<>(words).entrySet()) {
                    word = wordEntry.getKey();
                    WordData wordData = wordEntry.getValue();

                    if (word == null || wordData == null || wordData.getSpamFrequency() < 0 || wordData.getHamFrequency() < 0) {
                        System.err.printf("[ERROR] Palabra o frecuencias inválidas en '%s'.%n", word);
                        continue;
                    }

                    if (wordData.getSpamFrequency() == 0 && wordData.getHamFrequency() == 0) {
                        words.remove(word);
                        System.out.printf("[INFO] Palabra '%s' eliminada por frecuencias cero.%n", word);
                        continue;
                    }

                    WordCategory newCategory = determineCategory(wordData);

                    if (!currentCategory.equals(newCategory)) {
                        moveWordToCategory(word, wordData, currentCategory, newCategory, categoryMapMap);
                    }
                }
            }

            System.out.println("[INFO] Reasignación de palabras completada.");
            System.out.printf("[INFO] Total de palabras movidas: %d%n", movementLog.size());

        } catch (Exception e) {
            System.err.printf("[ERROR] Falló al procesar palabra '%s' en categoría '%s': %s%n",
                    word, currentCategory != null ? currentCategory.getJsonKey() : "desconocida", e.getMessage());
            e.printStackTrace();
        }
    }

    public WordCategory determineCategory(WordData wordData) {
        int hamFreq = wordData.getHamFrequency();
        int spamFreq = wordData.getSpamFrequency();
        int total = hamFreq + spamFreq;

        if (hamFreq <= 2 && spamFreq <= 2) {
            return WordCategory.UNASSIGNED_WORDS; // Paso 1: muy pocas apariciones
        }

        if (spamFreq == 0 && hamFreq > 2) {
            return WordCategory.HAM_INDICATORS; // Paso 2: solo ham
        }

        if (hamFreq == 0 && spamFreq > 2) { // Paso 3: solo spam
            if (spamFreq >= 15) return WordCategory.STRONG_SPAM_WORD;
            if (spamFreq >= 8)  return WordCategory.MODERATE_SPAM_WORD;
            return WordCategory.WEAK_SPAM_WORD;
        }

        double spamRatio = spamFreq * 1.0 / total;
        double hamRatio = hamFreq * 1.0 / total;
        double spamToHam = spamFreq * 1.0 / (hamFreq + 1);
        double hamToSpam = hamFreq * 1.0 / (spamFreq + 1);

        if (Math.abs(spamRatio - hamRatio) <= 0.15) {
            return WordCategory.NEUTRAL_BALANCED_WORD; // Paso 4: proporciones similares
        }

        if (hamRatio >= 0.70) {
            return WordCategory.HAM_INDICATORS; // Paso 5: predominio ham
        }

        if (spamRatio >= 0.85 && spamToHam >= 3.0 && spamFreq >= 8) {
            return WordCategory.STRONG_SPAM_WORD; // Paso 6.1: spam fuerte
        }

        if (spamRatio >= 0.66 && spamToHam >= 2.0) {
            return WordCategory.MODERATE_SPAM_WORD; // Paso 6.2: spam moderado
        }

        if (spamRatio >= 0.50 && spamToHam >= 1.2) {
            return WordCategory.WEAK_SPAM_WORD; // Paso 6.3: spam débil
        }

        return WordCategory.NEUTRAL_BALANCED_WORD; // Paso 7: por descarte
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
                updateExistingWordFrequencies(token, wordData); //
                System.out.printf("[INFO] Palabra '%s' en categoría protegida: solo se actualiza frecuencia.%n", token);
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
                if (token.trim().isEmpty()) {
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
        // Validación de token vacío o nulo
        if (token == null || token.trim().isEmpty()) {
            System.out.printf("[DEBUG] Ignorando token vacío o nulo al actualizar o reasignar: '%s'%n", token);
            return;
        }

        WordCategory currentCategory = null;

        // Buscar la categoría actual del token
        for (WordCategory category : WordCategory.values()) {
            if (dictionary.getCategory(category).containsKey(token)) {
                currentCategory = category;
                break;
            }
        }

        // Determinar la nueva categoría en base a las frecuencias
        WordCategory newCategory = determineCategory(wordData);

        // 1. Si la palabra ya existe y está en categoría protegida (no mover, solo actualizar)
        if (currentCategory != null && List.of(WordCategory.RARE_SYMBOLS, WordCategory.STOP_WORDS).contains(currentCategory)) {
            updateExistingWordFrequencies(token, wordData);
            System.out.printf("[INFO] Token '%s' en categoría protegida '%s' → solo se actualiza frecuencia.%n", token, currentCategory);
            return;
        }

        // 2. Si existe y cambia de categoría → actualizar primero, luego mover
        if (currentCategory != null && !currentCategory.equals(newCategory)) {
            updateExistingWordFrequencies(token, wordData);
            moveWordToCategory(token, dictionary.getCategory(currentCategory).get(token), currentCategory, newCategory, dictionary.getCategorizedWords());
            return;
        }

        // 3. Si existe y se mantiene en misma categoría → solo sumar frecuencias
        if (currentCategory != null) {
            updateExistingWordFrequencies(token, wordData);
            return;
        }

        // 4. Si no existía → agregar nuevo WordData al diccionario con su categoría calculada
        dictionary.addWordWithFrequencies(newCategory, token, wordData.getSpamFrequency(), wordData.getHamFrequency());
    }


    //////////////////////////////////////////////////////////////////////////












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

    public void addWordToLexemeRepository(String repositoryPath, String word, String lexeme) {
        resourcesHandler.addWordToLexemeRepository(repositoryPath, word, lexeme);
    }

    public void updateAddingLexemeRepositoryFromJsonList(String repositoryPath, String inputJsonPath) {
        resourcesHandler.updateAddingLexemeRepositoryFromJsonList(repositoryPath, inputJsonPath);
    }

    ///////////////////////////////////////////////////////////
    // ********* Metadata del SpamDictionary ******************
    ///////////////////////////////////////////////////////////
    public void exportMetadataJson(String relativePath, String exportedDictionaryPath) {
        try {
            // Obtener el objeto metadata directamente
            SpamDictionaryMetadata metadata = SpamDictionary.getInstance().getMetadata();

            // Extraer solamente el nombre del archivo del diccionario exportado para agregarlo al campo correspondiente del metadata
            String fileName = exportedDictionaryPath.substring(exportedDictionaryPath.lastIndexOf("/") + 1);
            metadata.setExportedDictionaryFileName(fileName);

            // Convertir a JSONObject manualmente
            JSONObject json = JsonUtils.spamDictionaryMetadataToJson(metadata);

            // Generar path único y guardar usando el handler
            String uniquePath = resourcesHandler.getUniqueFilePath(relativePath);
            resourcesHandler.saveJson(json, uniquePath);

            System.out.println("[INFO] Metadatos exportados exitosamente a: " + uniquePath);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar metadatos: " + e.getMessage(), e);
        }
    }





}//end
