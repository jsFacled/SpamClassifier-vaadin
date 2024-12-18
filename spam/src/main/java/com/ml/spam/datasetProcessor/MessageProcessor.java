package com.ml.spam.datasetProcessor;

import com.ml.spam.dictionary.models.NumCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.undefined.LabeledMessage;
import com.ml.spam.datasetProcessor.models.ProcessedMessage;
import com.ml.spam.utils.CsvUtils;
import com.ml.spam.utils.RegexUtils;
import com.ml.spam.utils.TextUtils;

import java.io.IOException;
import java.util.*;

import static com.ml.spam.utils.TextUtils.isValidMessageAndLabel;

public class MessageProcessor {

    public static List<List<WordData>> processToWordData(List<String[]> rawRows) {
        // Validar entrada de List rawRows
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("La lista de filas está vacía o es nula.");
        }
        // Inicializar estructura de salida y filas inválidas
        List<List<WordData>> validRows = new ArrayList<>();
        List<String[]> invalidRows = new ArrayList<>();

        // Iterar sobre las filas rawRows [mensaje,label]
        for (String[] row : rawRows) {
            // Validar mensaje y etiqueta: Tratar también las filas no válidas
            if (isValidMessageAndLabel(row)) {
                validRows.add(processValidRow(row));// Tokenizar el mensaje (palabras y símbolos raros) y agregar a validRows
            } else {
                invalidRows.add(row);
                System.err.println("Fila no válida encontrada: " + Arrays.toString(row));
            }
        }

        if (!invalidRows.isEmpty()) {
            System.err.println("Número total de filas no válidas: " + invalidRows.size());
        }

        return validRows;
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
        //tokenizeMessage en principio era solamente pasar a minuscula y separar por espacios
        List<String> tokens = TextUtils.tokenizeMessage(message);
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

    private static List<WordData> processValidRow(String[] row) {
        String message = row[0].trim();
        String label = row[1].trim();

        List<String> tokens = TextUtils.tokenizeMessage(message); // Tokenización básica
        List<WordData> wordDataList = new ArrayList<>();

        for (String token : tokens) {
            // Separar palabra, número y símbolos raros
            String[] splitToken = TextUtils.splitRareSymbolsAndNumbers(token);
            String wordPart = splitToken[0];
            String numberPart = splitToken[1];
            String rareSymbolPart = splitToken[2];

            // Procesar palabras
            if (!wordPart.isEmpty()) {
                WordData wordData = new WordData(wordPart);
                updateWordDataFrequency(wordData, label);
                wordDataList.add(wordData);
            }

            // Procesar números y clasificarlos en su categoría
            if (!numberPart.isEmpty()) {
                NumCategory numCategory = detectNumberCategory(numberPart);
                String numName = (numCategory != null) ? numCategory.toString() : "numlow";//si no se encuentra asigno numlow por defecto
                WordData numberData = new WordData(numName);
                updateWordDataFrequency(numberData, label);
                wordDataList.add(numberData);
            }

            // Procesar símbolos raros
            if (!rareSymbolPart.isEmpty()) {
                for (String symbol : rareSymbolPart.split(" ")) { // Procesar cada símbolo raro por separado
                    WordData symbolData = new WordData(symbol);
                    updateWordDataFrequency(symbolData, label);
                    wordDataList.add(symbolData);
                }
            }
        }

        return wordDataList;
    }

    private static NumCategory detectNumberCategory(String token) {
        if (RegexUtils.isNumDim(token)) return NumCategory.NUM_DIM;
        if (RegexUtils.isNumCal(token)) return NumCategory.NUM_CAL;
        if (RegexUtils.isNumMoney(token)) return NumCategory.NUM_MONEY;
        if (RegexUtils.isNumStat(token)) return NumCategory.NUM_STAT;
        if (RegexUtils.isNumCod(token)) return NumCategory.NUM_COD;
        if (RegexUtils.isNumUrl(token)) return NumCategory.NUM_URL;
        if (RegexUtils.isNumTel(token)) return NumCategory.NUM_TEL;
        if (RegexUtils.isNumIp(token)) return NumCategory.NUM_IP;
        if (RegexUtils.isNumLow(token)) return NumCategory.NUM_LOW;
        return null;
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
                List<String> tokens = tokenizeMessage(labeledMessage.getContent());

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

    /**
     * Tokeniza el contenido del mensaje en palabras.
     *
     * @param message Contenido del mensaje.
     * @return Lista de palabras (tokens).
     */
    private List<String> tokenizeMessage(String message) {
        // Tokenizar por espacios, eliminar puntuaciones y convertir a minúsculas
        String[] tokens = message.toLowerCase().replaceAll("[^a-zA-Záéíóúñ]", " ").split("\\s+");
        List<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                tokenList.add(token);
            }
        }
        return tokenList;
    }

}//END MessageProcessor
