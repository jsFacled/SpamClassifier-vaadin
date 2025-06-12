package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.datasetProcessor.utils.DuplicateMessageChecker;
import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;
import com.ml.spam.dictionary.models.MessageLabel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Programa de utilidad para normalizar un archivo de mensajes
 * escritos entre triples comillas. Se añade la etiqueta indicada,
 * se eliminan duplicados y el resultado se exporta a un nuevo archivo.
 */
public class NormalizeTripleQuotedMain {

    public static void main(String[] args) throws Exception {
        // Rutas por defecto para facilitar la ejecución directa
        String inputArg = args.length > 0 ? args[0] : "spam/src/main/resources/static/datasets/joined/joined_messages_triplecomillas_spam.txt";
        String outputArg = args.length > 1 ? args[1] : "joined_messages_triplecomillas_spam_normalized.txt";
        String labelArg = args.length > 2 ? args[2] : "spam";

        Path input = Paths.get(inputArg);
        Path output = Paths.get(outputArg);
        MessageLabel label = MessageLabel.valueOf(labelArg.toUpperCase());

        MessageNormalizerService service = new MessageNormalizerService();
        List<String> normalized = service.normalizeFromTripleQuotes(input, label);

        // Archivo temporal para aplicar la eliminación de duplicados
        Path temp = Files.createTempFile("normalized_triple", ".txt");
        service.exportToFile(normalized, temp);

        DuplicateMessageChecker checker = new DuplicateMessageChecker();
        checker.removeDuplicates(temp.toString(), output.toString(), DuplicateMessageChecker.InputFormat.LINE_BY_LINE);

        Files.deleteIfExists(temp);
        System.out.println("✅ Archivo generado: " + output);
    }
}