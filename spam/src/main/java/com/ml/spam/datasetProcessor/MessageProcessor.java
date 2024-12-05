package com.ml.spam.datasetProcessor;

import com.ml.spam.datasetProcessor.models.LabeledMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageProcessor {


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

    // Método auxiliar para limpiar comillas y espacios
    private static String cleanString(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("^\"|\"$", ""); // Eliminar comillas alrededor del texto
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

    /**
     * Actualiza el diccionario con las palabras tokenizadas.
     *
     * @param tokens Lista de palabras tokenizadas.
     * @param isSpam Indica si el mensaje es spam.
     */

    // Ver el tipo: String, Map, etc
    /*
    private void updateDictionary(List<String> tokens, boolean isSpam) {
        for (String token : tokens) {
            if (dictionaryService.wordExists(token)) {
                dictionaryService.updateWordFrequency(token, isSpam);
            } else {
                dictionaryService.addNewWord(token, isSpam);
            }
        }
    }

     */
}
