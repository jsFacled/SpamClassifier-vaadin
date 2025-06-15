package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.datasetProcessor.utils.DuplicateMessageChecker;
import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Normaliza un CSV donde cada línea contiene un mensaje y su etiqueta.
 * Permite pasar rutas de entrada y salida como argumentos.
 */
public class NormalizeLabeledMessagesCsvMain {
    private static final String inputPath = "spam/src/main/resources/static/datasets/joined/joined_messages_label.csv";

    public static void main(String[] args) throws Exception {
        String inputArg = args.length > 0 ? args[0]
                : inputPath;
        String outputArg = args.length > 1 ? args[1]
                : "joined_messages_labels_normalized_unique.txt";

        Path input = Paths.get(inputArg);
        Path output = Paths.get(outputArg);

        MessageNormalizerService service = new MessageNormalizerService();
        List<String> normalized = service.normalizeFromCsv(input);

        Path temp = Files.createTempFile("normalized_csv", ".txt");
        service.exportToFile(normalized, temp);

        DuplicateMessageChecker checker = new DuplicateMessageChecker();
        checker.removeDuplicates(temp.toString(), output.toString(),
                DuplicateMessageChecker.InputFormat.LINE_BY_LINE);

        Files.deleteIfExists(temp);
        System.out.println("✅ Archivo generado: " + output);
    }
}