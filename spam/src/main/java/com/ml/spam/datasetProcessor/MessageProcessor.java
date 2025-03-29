package com.ml.spam.datasetProcessor;

import com.ml.spam.dictionary.models.*;
import com.ml.spam.undefined.LabeledMessage;
import com.ml.spam.datasetProcessor.models.ProcessedMessage;
import com.ml.spam.utils.CsvUtils;
import com.ml.spam.utils.TextUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageProcessor {

    private static Map<CharSize, Map<String, Set<String>>> lexemeRepository;
    private static List<WordData> unknownEmojiList = new ArrayList<>();


    //Recibe los mensajes y repositorio de lexemas.
    public static List<List<WordData>> processToWordData(
            List<String[]> rawRows,

            Map<CharSize, Map<String, Set<String>>> lexemeRepository) {

        // Validar entrada de List rawRows
        if (rawRows == null || rawRows.isEmpty()) {
            throw new IllegalArgumentException("La lista de filas está vacía o es nula.");
        }

        // Inicializar estructura de salida y filas inválidas
        List<List<WordData>> wordDataLists = new ArrayList<>();
        List<String[]> invalidRows = new ArrayList<>();

        // Asignar valores a las variables estáticas del dictionary

        MessageProcessor.lexemeRepository = lexemeRepository;

        // Iterar sobre las filas rawRows [mensaje, label]
        int messageNumber=0;
        for (String[] row : rawRows) {
            messageNumber++;
            System.out.println("[\nINFO * * * In processToWordData * * * ] Se procesa el mensaje número: "+ messageNumber+" .\n");

            // Validar mensaje y etiqueta: Tratar también las filas no válidas
            if (TextUtils.isValidMessageAndLabel(row)) {
                // Tokenizar el mensaje (palabras y símbolos raros) y agregar a validRows
                wordDataLists.add(processValidRow(row));
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
        displayTokenListInConsole(tokens);

        // Paso 3: Inicializar la lista de WordData
        List<WordData> wordDataList = new ArrayList<>();

        // Paso 4: Recorrer la lista de tokens y procesar cada uno
        for (String token : tokens) {
            // Ignorar tokens vacíos o con solo caracteres de espacio
            if (token == null || token.trim().isEmpty()) {
                continue; // Salta al siguiente token
            }


                // Subpaso 4.1: Inicializar TokeenType y Clasificar el token
            TokenType tokenType = TextUtils.classifyToken(token);
                // Subpaso 4.2: Procesar según la clasificación del token
                processAllTokenSizes(token, tokenType, wordDataList, label);
              displayTokenInConsole(token,tokenType);

        }

        // Paso 5: Retornar la lista de WordData procesada
        return wordDataList;
    }

    private static void processAllTokenSizes(String token, TokenType tokenType, List<WordData> wordDataList, String label) {
        switch (tokenType) {

            case TEXT_NUM_SYMBOL:
                processTextNumSymbolToken(token, wordDataList, label);
                break;
            case NUM:
                processNumToken(token, wordDataList, label);
                break;
            case NUM_SYMBOL:
                processNumSymbolToken(token, wordDataList, label);
                break;
            case NUM_TEXT:
                processNumTextToken(token, wordDataList, label);
                break;
            case TEXT:
                processTextToken(token, wordDataList, label);
                break;
            case TEXT_SYMBOL:
                processTextSymbolToken(token, wordDataList, label);
                break;
            case SYMBOL:
                processSymbolToken(token, wordDataList, label);
                break;
            case CHAR:
                processCharToken(token, wordDataList, label);
                break;
            case UNASSIGNED:
            default:
                // En lugar de asignar UNASSIGNED, simplemente deja el token tal cual.
                processUnassignedToken(token, wordDataList, label);
                break;
        }
    }


    // Métodos auxiliares para cada tipo de token

    private static void processNumSymbolToken(String token, List<WordData> wordDataList, String label) {
//Map<String, Set<String>> textLexemes = lexemeRepository.get(CharSize.TEXT_LEXEMES);

        // Verificar si el token es un horario
        if (TextUtils.isTime(token)) {
            wordDataList.add(new WordData("numcalendar", label));
            return; // Clasificado como horario, salir temprano
        }

        // Divide números y símbolos para categorizarlos por separado
        String[] parts = token.split("(?<=\\d)(?=\\W)|(?<=\\W)(?=\\d)");
        System.out.println("El split de numSymbol es parts: "+Arrays.toString(parts));
        for (String part : parts) {
            System.out.println("Cada part in parts es: " + part);
            TokenType t = TextUtils.classifyToken(part);
            System.out.println("El tokenType de "+part +" es: "+t);
            switch (t){
                //buscar token en lexemerepository
                case CHAR  -> processCharToken(part, wordDataList, label);
                //case SYMBOL -> processAnySymbolToken(part,wordDataList,label);
                case SYMBOL -> processSymbolToken(part,wordDataList,label);

                case NUM -> processNumToken(part, wordDataList, label);
                case UNASSIGNED -> processUnassignedToken(part, wordDataList, label);

                default -> wordDataList.add(new WordData(part, label));

            }
        }
    }

    private static void processNumToken(String token, List<WordData> wordDataList, String label) {
        if (token.length() > 18) { // Si el número es demasiado largo
            wordDataList.add(new WordData("numsuperhigh", label));
            return;
        }

        try {
            int number = Integer.parseInt(token);
            String numCategory = number > 1000000 ? "numhigh" : "numlow";
            wordDataList.add(new WordData(numCategory, label));
        } catch (NumberFormatException e) {
            wordDataList.add(new WordData("invalidNumber", label));
        }
    }

    private static void processTextSymbolToken(String token, List<WordData> wordDataList, String label) {
        // Paso 1: Verificar si contiene '@' o es un correo electrónico
        if (token.contains("@")) {
            if (TextUtils.isWebEmail(token)) {
                wordDataList.add(new WordData("webemail", label)); // Asignar como correo electrónico
            } else {
                wordDataList.add(new WordData("@", label)); // Asignar como símbolo '@'
            }
            return; // Detener procesamiento adicional si contiene '@'
        }

        // Paso 2: Detectar si el token es una URL
        if (TextUtils.isWebAddress(token)) {
            String lowerCaseToken = token.toLowerCase(); // Convertir a minúsculas
            System.out.println("[DEBUG] Dirección web detectada dentro de TEXT_SYMBOL: " + lowerCaseToken);
            wordDataList.add(new WordData("webaddress", label)); // Asignar categoría directamente
            return; // Detener procesamiento adicional
        }

        // Paso 3: Separar texto y símbolos
        String wordPart = token.replaceAll("[^\\p{L}áéíóúÁÉÍÓÚñÑ]", ""); // Extraer letras
        String symbolPart = token.replaceAll("[\\p{L}áéíóúÁÉÍÓÚñÑ]", ""); // Extraer símbolos

        // Procesar la parte de texto
        if (!wordPart.isEmpty()) {
            String subCategory = findSubcategoryForToken(wordPart); // Buscar subcategoría en lexemesRepository
            // Asignar texto directamente
            wordDataList.add(new WordData(Objects.requireNonNullElse(subCategory, wordPart), label)); // Asignar subcategoría
        }

        // Procesar la parte de símbolos
        if (!symbolPart.isEmpty()) {
            for (char symbol : symbolPart.toCharArray()) {
                String symbolStr = String.valueOf(symbol);
                String subCategory = findSubcategoryForToken(symbolStr); // Buscar subcategoría en lexemesRepository
                // Asignar símbolo directamente
                wordDataList.add(new WordData(Objects.requireNonNullElse(subCategory, symbolStr), label)); // Asignar subcategoría
            }
        }
    }

    private static void processTextToken(String token, List<WordData> wordDataList, String label) {

        if (TextUtils.hasAccent(token)) {
            processAccentedWord(token, wordDataList, label);
        //    System.out.println("[DEBUG] Token tiene acento: " + token);
        } else {
            // Busca la subcategoría directamente
            String subCategory = findSubcategoryForToken(token);
            if (subCategory != null) {
                wordDataList.add(new WordData(subCategory, label));
            } else {
                wordDataList.add(new WordData(token, label));
            }
        }
    }

    private static void processNumTextToken(String token, List<WordData> wordDataList, String label) {
        // Dividir el token en dos partes
        String[] parts = TextUtils.splitNumberAndText(token);
        String firstPart = parts[0];
        String secondPart = parts[1];

        System.out.println("[DEBUG processNumText ] firstPart : <" + firstPart + ">");
        System.out.println("[DEBUG processNumText ] secondPart : <" + secondPart + ">");

        // Clasificar partes
        String numberPart = null;
        String textPart = null;

        if (firstPart.matches("\\d+")) { // Si es un número
            numberPart = firstPart;
            textPart = secondPart; // La otra parte es texto
        } else if (secondPart.matches("\\d+")) { // Si la segunda parte es un número
            numberPart = secondPart;
            textPart = firstPart; // La otra parte es texto
        }

        // Priorizar textPart si está en lexemeRepository
        if (textPart != null && !textPart.isEmpty()) {
            String subCategory = findSubcategoryForToken(textPart);
            if (subCategory != null) {
                wordDataList.add(new WordData(subCategory, label));
                return; // Ignorar numberPart si textPart está categorizado
            }
        }

        // Procesar numberPart si está presente
        if (numberPart != null && !numberPart.isEmpty()) {
            processNumToken(numberPart, wordDataList, label);
        }

        // Procesar textPart como texto genérico si no está en lexemeRepository
        if (textPart != null && !textPart.isEmpty()) {
            wordDataList.add(new WordData(textPart, label));
        }
    }

    private static void processTextNumSymbolToken(String token, List<WordData> wordDataList, String label) {
        // Paso 1: Verificar si contiene '@' o es un correo electrónico
        if (token.contains("@")) {
            if (TextUtils.isWebEmail(token)) {
                wordDataList.add(new WordData("webemail", label)); // Asignar como correo electrónico
            } else {
                wordDataList.add(new WordData("@", label)); // Asignar como símbolo '@'
            }
            return; // Detener procesamiento adicional si contiene '@'
        }

        // Paso 2: Detectar si el token es una URL
        if (TextUtils.isWebAddress(token)) {
            String lowerCaseToken = token.toLowerCase(); // Convertir a minúsculas
            System.out.println("[DEBUG] Dirección web detectada: " + lowerCaseToken);
            wordDataList.add(new WordData("webaddress", label)); // Asignar categoría directamente
            return; // Detener procesamiento adicional
        }

        // Paso 3: Continuar con el procesamiento estándar para otros tokens
        String[] components = TextUtils.splitRareSymbolsAndNumbers(token);
        for (String component : components) {
            if (component.isEmpty()) {
                continue;
            }

            if (TextUtils.isSymbolToken(component)) {
                for (char symbol : component.toCharArray()) {
                    String symbolStr = String.valueOf(symbol);
                    String subCategory = findSubcategoryForToken(symbolStr);
                    // Asignar directamente
                    wordDataList.add(new WordData(Objects.requireNonNullElse(subCategory, symbolStr), label)); // Asignar subcategoría
                }
            } else if (TextUtils.isTextToken(component)) {
                processTextToken(component, wordDataList, label);
            } else if (TextUtils.isNumericToken(component)) {
                processNumToken(component, wordDataList, label);
            } else {
                wordDataList.add(new WordData(component, label)); // Otros casos
            }
        }
    }

    private static void processCharToken(String token, List<WordData> wordDataList, String label) {
        // Obtener las subcategorías de ONE_CHAR en lexemeRepository
        Map<String, Set<String>> subCategories = lexemeRepository.get(CharSize.ONE_CHAR);

        // Buscar token en las subcategorías
        String subCategory = subCategories.entrySet().stream()
                .filter(entry -> entry.getValue().contains(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        // Si se encuentra, asignar la subcategoría
        if (subCategory != null) {
            wordDataList.add(new WordData(subCategory, label));
        } else {
            // Token no categorizado
            wordDataList.add(new WordData(token, label));
        }
    }

    private static void processSymbolToken(String token, List<WordData> wordDataList, String label) {
        if (TextUtils.containsEmoji(token)) {
            processEmojiToken(token, wordDataList, label); // Delegar el manejo de emojis
        } else {
            processOtherSymbols(token, wordDataList, label);
        }
    }

    private static void processEmojiToken(String token, List<WordData> wordDataList, String label) {
        // Extraer los emojis válidos del token
        List<String> emojis = extractCompleteEmojis(token);

        for (String emoji : emojis) {
            if (isEmojiInRepository(emoji)) {
                // Si el emoji es válido, agregarlo con la categoría genérica
                wordDataList.add(new WordData("lexemeemojis", label));
            } else {
                // Si el emoji no es válido, manejarlo como "unknownemojis"
                System.err.println("[WARN] Emoji no reconocido: " + emoji);
                addOrUpdateUnknownEmoji(wordDataList, label);
            }
        }
    }
    private static boolean isEmojiInRepository(String emoji) {
        return lexemeRepository.get(CharSize.ONE_CHAR).getOrDefault("lexemeemojis", Collections.emptySet()).contains(emoji) ||
                lexemeRepository.get(CharSize.TWO_CHARS).getOrDefault("lexemeemojis", Collections.emptySet()).contains(emoji);
    }


    private static void addOrUpdateUnknownEmoji(List<WordData> wordDataList, String label) {
        String unknownEmojiKey = "unknownemoji";

        // Verificar si `unknownemoji` ya está en wordDataList
        WordData unknownEmojiData = wordDataList.stream()
                .filter(wordData -> unknownEmojiKey.equals(wordData.getWord()))
                .findFirst()
                .orElseGet(() -> {
                    WordData newWordData = new WordData(unknownEmojiKey, 0, 0); // Frecuencias iniciales en 0
                    wordDataList.add(newWordData); // Agregar si no existe
                    return newWordData;
                });

        // Incrementar la frecuencia correspondiente
        updateWordDataFrequency(unknownEmojiData, label);
        System.out.println("[INFO] Frecuencia de 'unknownemoji' actualizada. Label: " + label);
    }


    private static String findSubcategoryForToken(String token) {
        if (token == null || token.isEmpty()) {
            return null; // Token inválido
        }

        int tokenLength = token.length();
        CharSize charSize = getCharSize(tokenLength);

        // Buscar en la categoría específica
        Map<String, Set<String>> subCategories = lexemeRepository.get(charSize);
        if (subCategories != null) {
            for (Map.Entry<String, Set<String>> subCategoryEntry : subCategories.entrySet()) {
                Set<String> words = subCategoryEntry.getValue();
                if (words.contains(token)) {
                    return subCategoryEntry.getKey(); // Retorna la subcategoría si encuentra el token
                }
            }
        }

        return null; // Token no encontrado
    }

    private static CharSize getCharSize(int length) {
        if (length > 10) {
            return CharSize.OVER_TEN_CHARS;
        }
        for (CharSize charSize : CharSize.values()) {
            if (charSize.getSize() == length) {
                return charSize;
            }
        }
        return null; // No debería suceder si el enum está completo
    }


    private static void processAccentedWord(String token, List<WordData> wordDataList, String label) {
        System.out.println("[DEBUG] Verificando token con tilde en lexemesRepository: " + token);

        // Paso 1: Intentar encontrar el token con tilde directamente en lexemesRepository
        String subCategory = findSubcategoryForToken(token);
        if (subCategory != null) {
            System.out.println("[DEBUG] Token con tilde encontrado en lexemesRepository. Subcategoría: " + subCategory);
            wordDataList.add(new WordData(subCategory, label));
            return;
        }

        // Paso 2: Normalizar el token eliminando la tilde
        String normalizedToken = TextUtils.removeAccents(token);
        System.out.println("[DEBUG] Token con tilde no encontrado. Normalizado: " + token + " -> " + normalizedToken);

        // Paso 3: Buscar el token normalizado en lexemesRepository
        subCategory = findSubcategoryForToken(normalizedToken);
        if (subCategory != null) {
            System.out.println("[DEBUG] Token sin tilde encontrado en lexemesRepository. Subcategoría: " + subCategory);
            wordDataList.add(new WordData(subCategory, label));
        } else {
            // Paso 4: Si no se encuentra, usar el token normalizado como valor final
            System.out.println("[DEBUG] Token sin tilde no encontrado en lexemesRepository. Usando token sin tilde: " + normalizedToken);
            wordDataList.add(new WordData(normalizedToken, label));
        }
    }



    /**
     * Extrae emojis completos de un token, construyendo secuencias válidas de puntos de código.
     * - Los emojis se validan contra la categoría `lexemeemojis` en `oneChar` y `twoChars`.
     * - Si un emoji no es reconocido, se registra un mensaje de advertencia.
     * - Evita acumulaciones innecesarias limitando la longitud de las secuencias a procesar.
     *
     * @param token El token que potencialmente contiene emojis.
     * @return Una lista de emojis completos extraídos del token.
     */
    private static List<String> extractCompleteEmojis(String token) {
        List<String> emojis = new ArrayList<>();
        int[] codePoints = token.codePoints().toArray();
        StringBuilder emojiBuilder = new StringBuilder();

        // Preprocesar el repositorio de emojis para búsquedas rápidas
        Set<String> validEmojis = new HashSet<>();
        for (CharSize charSize : Arrays.asList(CharSize.ONE_CHAR, CharSize.TWO_CHARS)) {
            validEmojis.addAll(
                    lexemeRepository.getOrDefault(charSize, Collections.emptyMap())
                            .getOrDefault("lexemeemojis", Collections.emptySet())
            );
        }

        for (int codePoint : codePoints) {
            emojiBuilder.append(new String(Character.toChars(codePoint)));
            String candidate = emojiBuilder.toString();

            // Verificar si la secuencia acumulada es un emoji válido
            if (validEmojis.contains(candidate)) {
                emojis.add(candidate);
                emojiBuilder.setLength(0); // Reiniciar para el próximo emoji
            } else if (emojiBuilder.length() > 4) { // Límite más flexible para secuencias largas
                System.err.println("[WARN] Secuencia no válida como emoji: " + emojiBuilder);
                emojiBuilder.setLength(0);
            }
        }

        // Manejar emojis parciales al final del token
        if (!emojiBuilder.isEmpty()) {
            System.err.println("[WARN] Emoji parcial no reconocido: " + emojiBuilder);
        }

        System.out.println("[INFO] Emojis extraídos: " + emojis);
        return emojis;
    }

    private static void processOtherSymbols(String token, List<WordData> wordDataList, String label) {
        Map<String, Set<String>> subCategories = lexemeRepository.get(TextUtils.determineCharSize(token));
        String subCategory = subCategories.entrySet().stream()
                .filter(entry -> entry.getValue().contains(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        wordDataList.add(new WordData(Objects.requireNonNullElse(subCategory, token), label));
    }

    // Métod mejorado para dividir emojis
    private static List<String> splitEmojis(String token) {
        List<String> emojis = new ArrayList<>();
        token.codePoints().forEach(codePoint -> emojis.add(new String(Character.toChars(codePoint))));
        return emojis;
    }



    private static void processAnySymbolToken(String token, List<WordData> wordDataList, String label) {
    /*   Map<String, Set<String>> textLexemes = lexemeRepository.get(CharSize.TEXT_LEXEMES);

        boolean isExcl = textLexemes != null && textLexemes.getOrDefault("excl", Collections.emptySet()).contains(token);
        boolean isLexsym = textLexemes != null && textLexemes.getOrDefault("lexsym", Collections.emptySet()).contains(token);
        boolean isMathop = textLexemes != null && textLexemes.getOrDefault("mathop", Collections.emptySet()).contains(token);

        if (isExcl) {
            wordDataList.add(new WordData("excl", label));
        } else if (isLexsym) {
            wordDataList.add(new WordData("lexsym", label));
        } else if (isMathop) {
            wordDataList.add(new WordData("mathop", label));
        } else {
            wordDataList.add(new WordData(token, label));
        }
    */
    }


    private static void processUnassignedToken(String token, List<WordData> wordDataList, String label) {
        System.out.println("[INFO processUnassignedToken] Procesando UNASSIGNED token: " + "<< " + token + " >>");

        // Busca directamente la subcategoría para el token
        String subCategory = findSubcategoryForToken(token);
        if (subCategory != null) {
            System.out.println("El TokenInLexemes ES: " + subCategory);
            wordDataList.add(new WordData(subCategory, label));
        } else {
            wordDataList.add(new WordData(token, label));
        }
    }




    private static String getTokenIfIsInLexemeRepository(String token) {
        String lexeme = getSubcategoryForToken(token);
        System.out.println("Estamos en getTokenIfIsInLexemeRepository: "+lexeme);
       return lexeme;
         }



    private static String getSubcategoryForToken(String token) {
        for (Map.Entry<CharSize, Map<String, Set<String>>> categoryEntry : lexemeRepository.entrySet()) {
            Map<String, Set<String>> subCategories = categoryEntry.getValue();

            for (Map.Entry<String, Set<String>> subCategoryEntry : subCategories.entrySet()) {
                String subCategory = subCategoryEntry.getKey();
                Set<String> words = subCategoryEntry.getValue();

                if (words.contains(token)) {
                    return subCategory; // Retorna la subcategoría si se encuentra el token
                }
            }
        }
        return "UNKNOWN_CATEGORY"; // Retorna un valor predeterminado si no se encuentra
    }

    private static void displayTokenInConsole(String token, TokenType tokenType) {
        System.out.println("\n [DEBUG * * * IN procesValidRow 4.1 * * * ] >>  token: [ " + token+ "] Es de tipo: "+tokenType + "\n");

    }

    private static void displayTokenListInConsole(List<String> tokens) {
        System.out.println("\n [DEBUG * * * IN processValidRow] >> tokens List para clasificar : [ " + tokens+ " ]\n");

    }



    private static void createAndAddWordData(String word, String label, List<WordData> wordDataList) {
    WordData wordData = new WordData(word); // Crear un objeto WordData
    updateWordDataFrequency(wordData, label); // Actualizar la frecuencia según el label (spam/ham)
    wordDataList.add(wordData); // Agregar a la lista wordDataList
}





    private static void updateWordDataFrequency(WordData wordData, String label) {
        if (MessageLabel.SPAM.getKey().equalsIgnoreCase(label)) {
            wordData.incrementSpamFrequency(1);
        } else if (MessageLabel.HAM.getKey().equalsIgnoreCase(label)) {
            wordData.incrementHamFrequency(1);
        }
    }




    @Deprecated
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

            // Crear y agregar el ProcessedMessage usando el métod auxiliar
            processedMessages.add(createProcessedMessage(row));
        }

        return processedMessages;
    }



    @Deprecated    // Métod auxiliar para procesar una fila en un ProcessedMessage
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

    //procesa listas crudas en labeledmessages, por ahora está obsoleta
    @Deprecated
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
     * Convierte una línea CSV en un objeto LabeledMessage.
     *
     * @param line Línea del archivo CSV.
     * @return Objeto LabeledMessage con el mensaje y la etiqueta.
     */
    @Deprecated
    private LabeledMessage readLineAndParse(String line) {
        String[] parts = line.split(",", 2); // Divide en contenido del mensaje y etiqueta
        String message = parts[0].trim();
        String label = parts[1].trim();
        return new LabeledMessage(message, label);
    }


    private static void processTokenByOneDigit(String token, TokenType tokenType, List<WordData> wordDataList, String label)  {
        switch (tokenType){
            //buscar token en lexemerepository
            case CHAR  -> processCharToken(token, wordDataList, label);
            case SYMBOL -> processSymbolToken(token,wordDataList,label);
            case NUM -> processNumToken(token, wordDataList, label);
        }
    }
    private static void processTokenByOneChar(String token, List<WordData> wordDataList, String label)  {
        if(TextUtils.isNumTextToken(token)){
            processNumToken(token, wordDataList,label);
        }else {
            findSubcategoryForToken(token);
        }

    }

}//END MessageProcessor
