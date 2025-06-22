package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;
import com.ml.spam.handlers.ResourcesHandler;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Normaliza un archivo CSV con mensajes etiquetados (mensaje,label).
 * Quita comillas innecesarias, unifica espacios y elimina duplicados.
 * Exporta el resultado limpio en formato CSV.
 */
public class NormalizeLabeledMessagesCsvMain {

    public static void main(String[] args) throws Exception {
      //  String inputArg = args.length > 0 ? args[0] : FilePathsConfig.MODEL_ORIGINAL_TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH;
      //  String outputArg = args.length > 1 ? args[1] : "labeled_original_test_messages_normalized.csv";

      //  String inputArg = args.length > 0 ? args[0] : FilePathsConfig.MODEL_ORIGINAL_TRAIN_MESSAGES_CSV_ESPAÑOL_PATH;
       // String outputArg = args.length > 1 ? args[1] : "labeled_original_train_messages_normalized.csv";

        String inputArg = args.length > 0 ? args[0] : FilePathsConfig.IA_GENERATED_LABELED_CSV_PATH;
        String outputArg = args.length > 1 ? args[1] : "labeled_ia_messages_normalized.csv";

        ResourcesHandler handler = new ResourcesHandler();
        List<String[]> rawRows = handler.loadQuotedOrPlainLabeledTxtFileAsMessages(inputArg);

        MessageNormalizerService service = new MessageNormalizerService();
        List<String> normalizedLines = service.normalizeFromCsv(rawRows);

        // Eliminar duplicados
        Set<String> unique = new LinkedHashSet<>(normalizedLines);

        // Exportar resultado
        handler.exportLabeledLinesToCsvFile(List.copyOf(unique), outputArg);

        System.out.println("✅ Archivo generado: " + outputArg);
        System.out.println("Total mensajes normalizados únicos: " + unique.size());
    }
}
