package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;
import com.ml.spam.dictionary.models.MessageLabel;
import com.ml.spam.handlers.ResourcesHandler;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Normaliza un archivo de mensajes escritos entre triples comillas.
 * Cada mensaje se convierte en una sola línea y se añade su etiqueta.
 * Se eliminan duplicados. Las triple comillas ya fueron removidas por ResourcesHandler.
 */
public class NormalizeTripleQuotedMain {

    public static void main(String[] args) throws Exception {
      // String inputArg = args.length > 0 ? args[0] : FilePathsConfig.MODEL_ORIGINAL_CORREOS_SPAM_FAC_TXT_PATH;
       // String outputArg = args.length > 1 ? args[1] : "original_triplecomillas_spam_normalized.txt";
      //  String labelArg = args.length > 2 ? args[2] : "spam";


        String inputArg = args.length > 0 ? args[0] : FilePathsConfig.IA_GENERATED_TRIPLECUOTES_SPAM_PATH;
       String outputArg = args.length > 1 ? args[1] : "static/datasets/normalized/ia_triplecomillas_spam_normalized.txt";
        String labelArg = args.length > 2 ? args[2] : "spam";

       // String inputArg = args.length > 0 ? args[0] : FilePathsConfig.IA_GENERATED_TRIPLECUOTES_HAM_PATH;
      // String outputArg = args.length > 1 ? args[1] : "ia_triplecomillas_ham_normalized.txt";
       // String labelArg = args.length > 2 ? args[2] : "ham";

        ResourcesHandler handler = new ResourcesHandler();
        List<String> rawBlocks = handler.loadTripleQuotedTxtFileAsMessages(inputArg);
        MessageLabel label = MessageLabel.valueOf(labelArg.toUpperCase());

        MessageNormalizerService service = new MessageNormalizerService();
        List<String> normalizedLines = service.normalizeFromTripleQuoteBlocks(rawBlocks, label);

        // Eliminar duplicados
        Set<String> unique = new LinkedHashSet<>(normalizedLines);

        // Exportar con handler en formato CSV (mensaje,label)
        handler.exportLabeledLinesToCsvFile(List.copyOf(unique), outputArg);

        System.out.println("✅ Archivo generado: " + outputArg);
        System.out.println("Total mensajes normalizados únicos: " + unique.size());
    }
}
