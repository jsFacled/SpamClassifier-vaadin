package com.ml.spam.datasetProcessor;

import com.ml.spam.dictionary.models.LexemeRepositoryCategories;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.TokenType;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.undefined.LabeledMessage;
import com.ml.spam.datasetProcessor.models.ProcessedMessage;
import com.ml.spam.utils.CsvUtils;
import com.ml.spam.utils.TextUtils;

import java.io.IOException;
import java.util.*;

public class MessageProcessor {

    private static Map<String, SpamDictionary.Pair> accentPairs;
    private static Map<LexemeRepositoryCategories, Set<String>> lexemeRepository;


    //Recibe los mensajes, accentpairs y repositorio de lexemas.
    public static List<List<WordData>> processToWordData(List<String[]> rawRows, Map<String, SpamDictionary.Pair> accentPairs, Map<LexemeRepositoryCategories, Set<String>> lexemeRepository ) {

        // Validar entrada de List rawRows
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("La lista de filas está vacía o es nula.");
        }

        // Inicializar estructura de salida y filas inválidas
        List<List<WordData>> wordDataLists = new ArrayList<>();
        List<String[]> invalidRows = new ArrayList<>();

        // Asignar valores a las variables estáticas del dictionary
        MessageProcessor.accentPairs = accentPairs;
        MessageProcessor.lexemeRepository = lexemeRepository;

        // Iterar sobre las filas rawRows [mensaje,label]
        for (String[] row : rawRows) {
            // Validar mensaje y etiqueta: Tratar también las filas no válidas
            if (TextUtils.isValidMessageAndLabel(row)) {
                wordDataLists.add(processValidRow(row));// Tokenizar el mensaje (palabras y símbolos raros) y agregar a validRows
            } else {
                invalidRows.add(row);
                System.err.println("Fila no válida encontrada: " + Arrays.toString(row));
            }
        }

        if (!invalidRows.isEmpty()) {
            System.err.println("Número total de filas no válidas: " + invalidRows.size());
        }
        return wordDataLists;
    }

    private static List<WordData> processValidRow(String[] row) {
        // Paso 1: Separar mensaje y etiqueta (label)
        String message = row[0].trim();
        String label = row[1].trim();

        // Paso 2: Tokenización básica del mensaje
        List<String> tokens = TextUtils.splitMessageAndLowercase(message);

        System.out.println("\n [DEBUG] >> tokens List: " + tokens+ "\n");

        // Paso 3: Inicializar la lista de WordData
        List<WordData> wordDataList = new ArrayList<>();

        // Paso 4: Recorrer la lista de tokens y procesar cada uno
        for (String token : tokens) {
            // Subpaso 4.1: Clasificar el token
            TokenType type = TextUtils.classifyToken(token);

            System.out.println("\n [DEBUG 4.1] >>  token: -" + token+ "- Es de tipo: "+type + "\n");

            // Subpaso 4.2: Procesar según la clasificación del token
            switch (type) {
                case NUM:
                    processNumToken(token, wordDataList, label);
                    break;
                case TEXT:
                    processTextToken(token, wordDataList, label);
                    break;
                case NUM_TEXT:
                    processNumTextToken(token, wordDataList, label);
                    break;
                case NUM_SYMBOL:
                    processNumSymbolToken(token, wordDataList, label);
                    break;
                case TEXT_SYMBOL:
                    processTextSymbolToken(token, wordDataList, label);
                    break;
                case TEXT_NUM_SYMBOL:
                    processTextNumSymbolToken(token, wordDataList, label);
                    break;
                case CHAR:
                    processCharToken(token, wordDataList, label);
                    break;
                case SYMBOL:
                    processSymbolToken(token, wordDataList, label);
                    break;
                case UNASSIGNED:
                default:
                    // En lugar de asignar UNASSIGNED, simplemente deja el token tal cual.
                    processUnassignedToken(token, wordDataList, label);
                    break;
            }
        }

        // Paso 5: Retornar la lista de WordData procesada
        return wordDataList;
    }

    private static void processNumSymbolToken(String token, List<WordData> wordDataList, String label) {
        // Divide números y símbolos para categorizarlos por separado
        String[] parts = token.split("(?<=\\d)(?=\\W)|(?<=\\W)(?=\\d)");

        for (String part : parts) {
            if (part.matches("\\d+")) {
                processNumToken(part, wordDataList, label); // Proceso de número
            } else if (part.matches("\\W+")) {
                processSymbolToken(part, wordDataList, label); // Proceso de símbolo
            }
        }
    }

    private static void processTextSymbolToken(String token, List<WordData> wordDataList, String label) {
        // Divide el token en partes: texto y símbolos
        String[] parts = token.split("(?<=\\p{L})(?=\\W)|(?<=\\W)(?=\\p{L})");

        for (String part : parts) {
            if (part.matches("\\p{L}+")) {
                // Parte es texto
                wordDataList.add(new WordData(part, label));
            } else if (part.matches("\\W+")) {
                // Parte es un símbolo raro
                wordDataList.add(new WordData(part, label));
            }
        }
    }

    // Métodos auxiliares para cada tipo de token
    private static void processNumToken(String token, List<WordData> wordDataList, String label) {
        int number = Integer.parseInt(token);
        String numCategory = number > 999 ? "NUMhigh" : "NUMlow";
        wordDataList.add(new WordData(numCategory, label));
    }

    private static void processTextToken(String token, List<WordData> wordDataList, String label) {
        if (TextUtils.hasAccent(token)) {
            String normalized = TextUtils.removeAccents(token);
            if (isInAccentPairs(normalized)) {
                wordDataList.add(new WordData(getAccentPairCategory(normalized), label));
                return;
            }
        }
        if (isInTextLexemeRepo(token)) {
            wordDataList.add(new WordData(getLexemeCategory(token), label));
        } else {
            wordDataList.add(new WordData(token, label));
        }
    }

    private static void processNumTextToken(String token, List<WordData> wordDataList, String label) {
        String[] parts = TextUtils.splitNumberAndText(token);
        processNumToken(parts[0], wordDataList, label);
        processTextToken(parts[1], wordDataList, label);
    }

    private static void processTextNumSymbolToken(String token, List<WordData> wordDataList, String label) {
        String[] components = TextUtils.splitRareSymbolsAndNumbers(token);
        processTextToken(components[0], wordDataList, label);
        processNumToken(components[1], wordDataList, label);
        processSymbolToken(components[2], wordDataList, label);
    }

    private static void processCharToken(String token, List<WordData> wordDataList, String label) {
        if (token.length() == 1) {
            wordDataList.add(new WordData(token, label));
        } else {
            wordDataList.add(new WordData(token, label));
        }
    }

    private static void processSymbolToken(String token, List<WordData> wordDataList, String label) {


        // Verificar si el token clasificado como SYMBOL es un
        if (TextUtils.isEmoji(token)) {
            if (isInSpamEmojiRepo(token)) {
                wordDataList.add(new WordData("spamemoji", label));
            } else if (isInHamEmojiRepo(token)) {
                wordDataList.add(new WordData("hamemoji", label));
            } else {
                // Emoji desconocido todo: agregar al categorizedWords
                wordDataList.add(new WordData("unknownemoji", label));
            }
        } else {
            // Procesar símbolo genérico
            wordDataList.add(new WordData(token, label));
        }
    }

    private static boolean isInSpamEmojiRepo(String token) {
        // Aquí verificas si el token está en la categoría "spamemoji" del repositorio
        return LexemeRepository.getInstance().getSpamEmojis().contains(token);
    }

    private static boolean isInHamEmojiRepo(String token) {
        // Aquí verificas si el token está en la categoría "hamemoji" del repositorio
        return LexemeRepository.getInstance().getHamEmojis().contains(token);
    }

    private static void processUnassignedToken(String token, List<WordData> wordDataList, String label) {
        // Agrega el token sin clasificar para que el Service decida cómo manejarlo
        System.out.println("[DEBUG] Token UNASSIGNED detectado: " + token);
        wordDataList.add(new WordData(token, label));
    }


    // Métodos adicionales para validaciones y repositorios
    private static boolean isInAccentPairs(String token) {
        // Validar en el mapa de accentPairs
        return false; // Implementar según lógica
    }

    private static String getAccentPairCategory(String token) {
        // Obtener categoría del par acentuado
        return "ACCENT_PAIR_CATEGORY"; // Implementar según lógica
    }

    private static boolean isInTextLexemeRepo(String token) {
        // Validar si está en el repositorio de lexemas
        return false; // Implementar según lógica
    }

    private static String getLexemeCategory(String token) {
        // Obtener categoría de lexema
        return "LEXEME_CATEGORY"; // Implementar según lógica
    }

    private static boolean isRareSymbol(String token) {
        // Validar si es un símbolo raro
        return false; // Implementar según lógica
    }


    private static void createAndAddWordData(String word, String label, List<WordData> wordDataList) {
    WordData wordData = new WordData(word); // Crear un objeto WordData
    updateWordDataFrequency(wordData, label); // Actualizar la frecuencia según el label (spam/ham)
    wordDataList.add(wordData); // Agregar a la lista wordDataList
}


    public static List<ProcessedMessage> simpleProcess(List<String[]> rawRows) {
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("Las filas crudas están vacías o no son válidas.");
        }

        // Eliminar cabecera si está presente
        CsvUtils.removeHeaderIfPresent(rawRows);

        List<ProcessedMessage> processedMessages = new ArrayList<>();

        for (String[] row : rawRows) {
            // Validar fila
            if (!CsvUtils.isValidRow(row)) {
                System.err.println("Fila inválida encontrada: " + Arrays.toString(row));
                continue;
            }

            // Crear y agregar el ProcessedMessage usando el método auxiliar
            processedMessages.add(createProcessedMessage(row));
        }

        return processedMessages;
    }

    // Método auxiliar para procesar una fila en un ProcessedMessage
    private static ProcessedMessage createProcessedMessage(String[] row) {
        String message = row[0].trim(); // Mensaje
        String label = row[1].trim();   // Etiqueta

        // Tokenización y frecuencia de palabras
        //splitMessageAndLowercase en principio era solamente pasar a minuscula y separar por espacios
        List<String> tokens = TextUtils.splitMessageAndLowercase(message);
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String token : tokens) {
            wordFrequency.put(token, wordFrequency.getOrDefault(token, 0) + 1);
        }

        // Crear y devolver ProcessedMessage
        return new ProcessedMessage(
                tokens,
                wordFrequency,
                label,
                tokens.size(),
                0.0, // rareSymbolProportion no aplica aquí
                0.0  // stopWordFrequency no aplica aquí
        );
    }


    private static void updateWordDataFrequency(WordData wordData, String label) {
        if ("spam".equalsIgnoreCase(label)) {
            wordData.incrementSpamFrequency(1);
        } else if ("ham".equalsIgnoreCase(label)) {
            wordData.incrementHamFrequency(1);
        }
    }




    //procesa listas crudas en labeledmessages, por ahora está obsoleta
    public static List<LabeledMessage> process(List<String[]> rawRows) {
        // Verificar si la primera fila es una cabecera válida
        if (!rawRows.isEmpty()) {
            String headerMessage = rawRows.get(0)[0].toLowerCase(); // Primera columna de la cabecera
            String headerLabel = rawRows.get(0)[1].toLowerCase();   // Segunda columna de la cabecera

            // Si la cabecera corresponde a "mensaje" o "message" y "tipo" o "label", la eliminamos
            if ((headerMessage.equals("mensaje") || headerMessage.equals("message")) &&
                    (headerLabel.equals("tipo") || headerLabel.equals("label"))) {
                rawRows.remove(0); // Remover la fila de cabecera
            }
        }

        List<LabeledMessage> labeledMessages = new ArrayList<>();

        for (String[] row : rawRows) {
            // Validar que la fila tenga al menos dos columnas
            if (row.length >= 2) {
                String message = row[0].trim(); // Primera columna como mensaje
                String label = row[1].trim();   // Segunda columna como tipo o label
                labeledMessages.add(new LabeledMessage(message, label));
            } else {
                System.err.println("Fila inválida encontrada: " + Arrays.toString(row));
            }
        }

        return labeledMessages;
    }

    /**
     *
     *
     *  ******* A partir de acá dejo Código viejo por las dudas *******
     *
     * */

    /*
         //ver su implementaciòn ya que se cambió la estructura del diccionario de set a Map.


    private final SpamDictionaryService dictionaryService;

    // Constructor para inyectar el servicio del diccionario
    public CsvMessageProcessor(SpamDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
*/

    /**
     * Método principal: procesa el CSV y actualiza el diccionario.
     *
     * @param filePath Ruta del archivo CSV.
     * @throws IOException Si ocurre algún error al leer el archivo.
     **/
    /*
    public void processCsvAndUpdateDictionary(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Saltar la línea del encabezado

            while ((line = br.readLine()) != null) {
                // Paso 1: Parsear la línea
                LabeledMessage labeledMessage = readLineAndParse(line);

                // Paso 2: Tokenizar el mensaje
                List<String> tokens = splitMessageAndLowercase(labeledMessage.getContent());

                // Paso 3: Actualizar el diccionario
                updateDictionary(tokens, "spam".equalsIgnoreCase(labeledMessage.getLabel()));
            }
        }
    }
*/
    /**
     * Convierte una línea CSV en un objeto LabeledMessage.
     *
     * @param line Línea del archivo CSV.
     * @return Objeto LabeledMessage con el mensaje y la etiqueta.
     */
    private LabeledMessage readLineAndParse(String line) {
        String[] parts = line.split(",", 2); // Divide en contenido del mensaje y etiqueta
        String message = parts[0].trim();
        String label = parts[1].trim();
        return new LabeledMessage(message, label);
    }


}//END MessageProcessor
