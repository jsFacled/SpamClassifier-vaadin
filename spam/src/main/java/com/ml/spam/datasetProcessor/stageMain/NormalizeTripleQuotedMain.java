// ✅ Clase principal corregida
package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.utils.MessageNormalizerService;
import com.ml.spam.dictionary.models.MessageLabel;
import com.ml.spam.handlers.ResourcesHandler;

import java.nio.file.Path;
import java.util.List;

/**
 * Normaliza un archivo de mensajes escritos entre triples comillas.
 * Cada mensaje se convierte en una sola línea y se añade su etiqueta.
 * Las triple comillas ya fueron eliminadas por ResourcesHandler.
 */
public class NormalizeTripleQuotedMain {

    public static void main(String[] args) throws Exception {
        String inputArg = args.length > 0 ? args[0] : FilePathsConfig.MODEL_ORIGINAL_CORREOS_SPAM_FAC_TXT_PATH;
        String outputArg = args.length > 1 ? args[1] : "original_triplecomillas_spam_normalized.txt";
        String labelArg = args.length > 2 ? args[2] : "spam";

        ResourcesHandler handler = new ResourcesHandler();
        List<String> messages = handler.loadTripleQuotedTxtFileAsMessages(inputArg);
        MessageLabel label = MessageLabel.valueOf(labelArg.toUpperCase());

        MessageNormalizerService service = new MessageNormalizerService();
        List<String> normalized = service.normalizeFromTripleQuoteBlocks(messages, label);

        Path output = handler.resolvePath(outputArg);
        service.exportToFile(normalized, output);

        System.out.println("✅ Archivo generado: " + output);
        System.out.println("Total mensajes normalizados: " + normalized.size());
    }
}
