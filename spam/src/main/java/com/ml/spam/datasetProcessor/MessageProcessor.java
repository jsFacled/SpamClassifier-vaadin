package com.ml.spam.datasetProcessor;

import com.ml.spam.dictionary.models.*;
import com.ml.spam.undefined.LabeledMessage;
import com.ml.spam.datasetProcessor.models.ProcessedMessage;
import com.ml.spam.utils.CsvUtils;
import com.ml.spam.utils.TextUtils;

import java.util.*;

public class MessageProcessor {

    private static Map<String, SpamDictionary.Pair> accentPairs;
    private static Map<CharSize, Map<String, Set<String>>> lexemeRepository;

    //Recibe los mensajes, accentpairs y repositorio de lexemas.
    public static List<List<WordData>> processToWordData(
            List<String[]> rawRows,
            Map<String, SpamDictionary.Pair> accentPairs,
            Map<CharSize, Map<String, Set<String>>> lexemeRepository) {

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

        // Iterar sobre las filas rawRows [mensaje, label]
        for (String[] row : rawRows) {
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
        //isplayTokenListInConsole(tokens);

        // Paso 3: Inicializar la lista de WordData
        List<WordData> wordDataList = new ArrayList<>();

        // Paso 4: Recorrer la lista de tokens y procesar cada uno
        for (String token : tokens) {
            // Inicializar tokenType
            TokenType tokenType = TokenType.UNASSIGNED;
                // Subpaso 4.1: Procesar Clasificar el token
                tokenType = TextUtils.classifyToken(token);
                // Subpaso 4.2: Procesar según la clasificación del token
                processAllTokenSizes(token, tokenType, wordDataList, label);
               // displayTokenInConsole(token,tokenType);


          /*  // Subpaso 4.1: Procesar Clasificar el token
            if (TextUtils.isOneChar(token)) {
                tokenType = TextUtils.classifyTokenByOneDigit(token);
                processTokenByOneDigit(token, tokenType, wordDataList, label);
                displayTokenInConsole(token, tokenType);
            } else {
                tokenType = TextUtils.classifyToken(token);
                // Subpaso 4.2: Procesar según la clasificación del token
                processAllTokenSizes(token, tokenType, wordDataList, label);
                displayTokenInConsole(token,tokenType);
            }

           */
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
                case SYMBOL -> processAnySymbolToken(part,wordDataList,label);
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
      //  System.out.println("[DEBUG] Procesando token: " + token);

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

    private static String findSubcategoryForToken(String token) {
        for (Map.Entry<CharSize, Map<String, Set<String>>> categoryEntry : lexemeRepository.entrySet()) {
            Map<String, Set<String>> subCategories = categoryEntry.getValue();

            for (Map.Entry<String, Set<String>> subCategoryEntry : subCategories.entrySet()) {
                Set<String> words = subCategoryEntry.getValue();

                if (words.contains(token)) {
                    return subCategoryEntry.getKey(); // Retorna la subcategoría si se encuentra el token
                }
            }
        }
        return null; // Retorna null si no se encuentra
    }

    private static void processAccentedWord(String token, List<WordData> wordDataList, String label) {
        System.out.println("[DEBUG] Verificando token en lexemesRepository: " + token);

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


    /**
     * Procesa un token clasificado como SYMBOL, verificando si contiene emojis o pertenece a otras subcategorías.
     * - Si el token contiene emojis:
     *   Se divide en emojis individuales y se verifica si pertenecen a las categorías `spamemoji` o `hamemoji` en el `lexemeRepository`.
     * - Si el token no contiene emojis:
     *   Se busca en las subcategorías correspondientes según su tamaño en `lexemeRepository`.
     *
     * @param token El token a procesar.
     * @param wordDataList La lista donde se almacenarán los resultados del procesamiento en forma de objetos WordData.
     * @param label La etiqueta asociada al mensaje que contiene el token (e.g., spam/ham).
     */
    private static void processSymbolToken(String token, List<WordData> wordDataList, String label) {
        // Caso 1: Si el token contiene emojis
        if (TextUtils.containsEmoji(token)) {
            List<String> emojis = extractCompleteEmojis(token);
            for (String emoji : emojis) {
                String emojiCategory = lexemeRepository.get(CharSize.ONE_CHAR).entrySet().stream()
                        .filter(entry -> entry.getKey().equals("spamemoji") || entry.getKey().equals("hamemoji"))
                        .filter(entry -> entry.getValue().contains(emoji))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("unknownemoji");

                if ("unknownemoji".equals(emojiCategory)) {
                    System.err.println("[WARN] Emoji no reconocido: " + emoji);
                }

                wordDataList.add(new WordData(emojiCategory, label));
            }
            return; // Finalizar después de procesar emojis
        }

        // Caso 2: Procesar otros símbolos
        Map<String, Set<String>> subCategories = lexemeRepository.get(determineCharSize(token));
        String subCategory = subCategories.entrySet().stream()
                .filter(entry -> entry.getValue().contains(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        // Token no categorizado
        wordDataList.add(new WordData(Objects.requireNonNullElse(subCategory, token), label));
    }



    /**
     * Extrae emojis completos de un token, construyendo secuencias válidas de puntos de código.
     * - Los emojis se validan contra las categorías `spamemoji` y `hamemoji` en `lexemeRepository`.
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

        for (int codePoint : codePoints) {
            emojiBuilder.append(new String(Character.toChars(codePoint)));
            String candidate = emojiBuilder.toString();

            // Verificar si es un emoji completo
            boolean isEmoji = lexemeRepository.get(CharSize.ONE_CHAR).getOrDefault("spamemoji", Set.of()).contains(candidate) ||
                    lexemeRepository.get(CharSize.ONE_CHAR).getOrDefault("hamemoji", Set.of()).contains(candidate);

            if (isEmoji) {
                emojis.add(candidate);
                emojiBuilder.setLength(0); // Reiniciar para el próximo emoji
            } else if (emojiBuilder.length() > 2) { // Limitar acumulación
                emojiBuilder.setLength(0);
            }
        }

        // Verificar si hay un emoji incompleto al final
        if (!emojiBuilder.isEmpty()) {
            System.err.println("[WARN] Emoji parcial no reconocido: " + emojiBuilder);
        }

        return emojis;
    }


    // Métod mejorado para dividir emojis
    private static List<String> splitEmojis(String token) {
        List<String> emojis = new ArrayList<>();
        token.codePoints().forEach(codePoint -> emojis.add(new String(Character.toChars(codePoint))));
        return emojis;
    }




    private static void processEmojiToken(String token, List<WordData> wordDataList, String label) {
       /*
        Map<String, Set<String>> contextualLexemes
                = lexemeRepository. get(CharSize.CONTEXTUAL_LEXEMES);
        if (contextualLexemes != null) {
            if (contextualLexemes.getOrDefault("spamemoji", Collections.emptySet()).contains(token)) {
                wordDataList.add(new WordData("spamemoji", label));
            } else if (contextualLexemes.getOrDefault("hamemoji", Collections.emptySet()).contains(token)) {
                wordDataList.add(new WordData("hamemoji", label));
            } else {
                // Si no pertenece a ninguna categoría conocida, devuelve el token
                wordDataList.add(new WordData(token, label));
            }
        } else {
            // Si el repositorio de lexemas no está inicializado, devuelve el token
            wordDataList.add(new WordData(token, label));
        }

        */
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

    private static boolean isInLexemeRepository(String token) {
        return lexemeRepository != null && lexemeRepository.values().stream()
                .flatMap(subCategoryMap -> subCategoryMap.values().stream()) // Iterar sobre los conjuntos dentro de las subcategorías
                .anyMatch(set -> set.contains(token)); // Verificar si el token está en alguno de los conjuntos
    }


    // Métod auxiliar para determinar el tamaño de carácter
    private static CharSize determineCharSize(String token) {
        int length = token.length();
        if (length == 1) return CharSize.ONE_CHAR;
        if (length == 2) return CharSize.TWO_CHARS;
        if (length == 3) return CharSize.THREE_CHARS;
        if (length == 4) return CharSize.FOUR_CHARS;
        if (length == 5) return CharSize.FIVE_CHARS;
        if (length == 6) return CharSize.SIX_CHARS;
        if (length == 7) return CharSize.SEVEN_CHARS;
        if (length == 8) return CharSize.EIGHT_CHARS;
        if (length == 9) return CharSize.NINE_CHARS;
        if (length == 10) return CharSize.TEN_CHARS;
        return CharSize.OVER_TEN_CHARS;
    }
    private static String getTokenIfIsInLexemeRepository(String token) {
        String lexeme = getSubcategoryForToken(token);
        System.out.println("Estamos en getTokenIfIsInLexemeRepository: "+lexeme);
       return lexeme;
         }


    private static String getAccentPairCategory(String token) {
        // Obtener categoría del par acentuado
        return "ACCENT_PAIR_CATEGORY"; // Implementar según lógica
    }

    private static boolean isInTextLexemeRepo(String token) {
        // Validar si está en el repositorio de lexemas
        return false; // Implementar según lógica
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
/*
//Uso de api stram
    private static String getSubcategoryForToken(String token) {
        return lexemeRepository.entrySet().stream()
                .flatMap(categoryEntry -> categoryEntry.getValue().entrySet().stream())
                .filter(subCategoryEntry -> subCategoryEntry.getValue().contains(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("UNKNOWN_CATEGORY");
    }
*/
    private static boolean isRareSymbol(String token) {
        // Validar si es un símbolo raro
        return false; // Implementar según lógica
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



    private static void displayTokenInConsole(String token, TokenType tokenType) {
        System.out.println("\n [DEBUG 4.1] >>  token: -" + token+ "- Es de tipo: "+tokenType + "\n");

    }

    private static void displayTokenListInConsole(List<String> tokens) {
        System.out.println("\n [DEBUG] >> tokens List: " + tokens+ "\n");

    }
}//END MessageProcessor
