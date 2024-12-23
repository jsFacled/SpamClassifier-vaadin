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

public class MessageProcessor {

    public static List<List<WordData>> processToWordData(List<String[]> rawRows) {
        // Validar entrada de List rawRows
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("La lista de filas está vacía o es nula.");
        }
        // Inicializar estructura de salida y filas inválidas
        List<List<WordData>> wordDataLists = new ArrayList<>();
        List<String[]> invalidRows = new ArrayList<>();

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
        return null;
    }

    private static void processRareSymbolToken(String token, List<WordData> wordDataList, String label) {
    }

    private static void processWordToken(String token, List<WordData> wordDataList, String label) {
    }

    private static void processTokenWithTilde(String token, List<WordData> wordDataList, String label) {
    }

    public static void processNumberToken(String token, List<WordData> wordDataList, String label) {
        // 1. Separar el token en número y texto
        String[] parts = TextUtils.splitNumberAndText(token);
        String numberPart = parts[0];
        String textPart = parts[1];

        //********************************************
        //* * * *  2. Procesar la parte textual * * * *
        //*********************************************

            // Buscar la parte textual en las categorías de lexemas
            // Verificar en las que comienzan con "num".
        if (!textPart.isEmpty()) {
            String numCategory = findInNumCategories(textPart);
            if (numCategory != null) {
                // 2.a Si está en una categoría de "num"-->Asignar el token a esa categoría y agregar a wordDataList.
                createAndAddWordData(numCategory, label, wordDataList);

            } else {
                // 2.b Si no está en "num", buscar en categorías "lex"
                String lexCategory = findInLexCategories(textPart);
                    // Verificar si la palabra pertenece a una categoría léxica.
                if (lexCategory != null) {
                    // 2.b.1 Si está en una categoría léxica
                    // Asignar la categoría léxica y agregar a wordDataList.
                    createAndAddWordData(lexCategory, label, wordDataList);
                } else {
                    // 2.b.2 Si no está en ninguna categoría
                    // Crear un nuevo WordData con la palabra como texto y el label del mensaje.
                    createAndAddWordData(textPart, label, wordDataList);
                }
            }
        }
        //********************************************
        //* * * *  3. Procesar la parte numérica * * * *
        //*********************************************

        // Evaluar si el número es numlow o numhigh, y crear WordData correspondiente.
        if (!numberPart.isEmpty()) {
            String numType = Integer.parseInt(numberPart) > 999 ? "numhigh" : "numlow";
            createAndAddWordData(numType, label, wordDataList);
        }


        // 4. Agregar ambos objetos WordData a wordDataList
        // Asegurarse de que tanto el texto como el número se procesen y agreguen correctamente.

    }

    private static String findInLexCategories(String textPart) {
        return "revisar metodo";//todo revisar metodo
    }

    private static String findInNumCategories(String textPart) {
        return "revisar metodo";//todo revisar metodo
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
