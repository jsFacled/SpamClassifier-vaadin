package com.ml.spam.datasetProcessor;

import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.undefined.LabeledMessage;
import com.ml.spam.datasetProcessor.models.ProcessedMessage;
import com.ml.spam.utils.CsvUtils;

import java.io.IOException;
import java.util.*;

public class MessageProcessor {


    //Recibe filas crudas del service, tokeniza y crea lista de WordData, Devuelve Lista de Lista<palabra,WordData>
    public static List<List<WordData>> processToWordData(List<String[]> rawRows) {
        // Remover la cabecera si está presente
        CsvUtils.removeHeaderIfPresent(rawRows);

        // Estructura para almacenar el resultado final
        List<List<WordData>> result = new ArrayList<>();

        for (String[] row : rawRows) {
            // Validar la fila
            if (!CsvUtils.isValidRow(row)) {
                System.err.println("Fila inválida: " + Arrays.toString(row));
                continue; // Omitir filas inválidas
            }

            String message = row[0].trim(); // Contenido del mensaje
            String label = row[1].trim();   // Etiqueta (spam/ham)

            // Mapa temporal para consolidar las frecuencias dentro del mensaje
            Map<String, WordData> wordDataMap = new HashMap<>();
            List<String> tokens = CsvUtils.tokenizeMessage(message);

            for (String token : tokens) {
                // Separar símbolos raros de la palabra
                String[] splitToken = splitRareSymbols(token);

                // Procesar palabra (si existe)
                if (!splitToken[0].isEmpty()) {
                    WordData wordData = wordDataMap.getOrDefault(splitToken[0], new WordData(splitToken[0]));
                    if ("spam".equalsIgnoreCase(label)) {
                        wordData.incrementSpamFrequency(1); // Incrementar frecuencia de spam
                    } else {
                        wordData.incrementHamFrequency(1); // Incrementar frecuencia de ham
                    }
                    wordDataMap.put(splitToken[0], wordData);
                }

                // Procesar símbolo raro (si existe)
                if (!splitToken[1].isEmpty()) {
                    WordData symbolData = wordDataMap.getOrDefault(splitToken[1], new WordData(splitToken[1]));
                    if ("spam".equalsIgnoreCase(label)) {
                        symbolData.incrementSpamFrequency(1); // Incrementar frecuencia de spam
                    } else {
                        symbolData.incrementHamFrequency(1); // Incrementar frecuencia de ham
                    }
                    wordDataMap.put(splitToken[1], symbolData);
                }
            }

            // Convertir el mapa a una lista y agregar al resultado
            result.add(new ArrayList<>(wordDataMap.values()));
        }

        return result;
    }

    /**
     * Divide un token en dos partes: la palabra principal y los símbolos raros.
     *
     * @param token El token a dividir.
     * @return Un arreglo de dos elementos: [palabra, símbolo raro].
     */
    /**
     * Divide un token en dos partes: la palabra principal y los símbolos raros.
     * Palabras válidas incluyen acentos y caracteres especiales del idioma español.
     *
     * @param token El token a dividir.
     * @return Un arreglo de dos elementos: [palabra, símbolo raro].
     */
    private static String[] splitRareSymbols(String token) {
        // Regex: Mantiene palabras con letras, acentos y caracteres válidos en español.
        String wordPart = token.replaceAll("^[^\\p{L}áéíóúÁÉÍÓÚñÑ]+|[^\\p{L}áéíóúÁÉÍÓÚñÑ]+$", "");
        String rareSymbolsPart = token.replaceAll("[\\p{L}áéíóúÁÉÍÓÚñÑ]+", "");

        return new String[]{wordPart, rareSymbolsPart};
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
        List<String> tokens = CsvUtils.tokenizeMessage(message);
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
     *  ******* Código viejo *******
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



}
