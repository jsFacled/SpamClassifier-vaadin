package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;

import java.util.List;

/**
 * Programa principal para mostrar mensajes extraídos de un archivo con comillas simples o sin comillas y con etiquetas.
 */
public class ShowLabeledSingleLineMessagesMain {

    public static void main(String[] args) {
        String relativeIALabeledTextPath = args.length > 0 ? args[0] : FilePathsConfig.IA_GENERATED_LABELED_CSV_PATH;
        String relativeOriginalTrainLabeledTextPath = args.length > 0 ? args[0] : FilePathsConfig.MODEL_ORIGINAL_TRAIN_MESSAGES_CSV_ESPAÑOL_PATH;
        String relativeOriginalTestLabeledTextPath = args.length > 0 ? args[0] : FilePathsConfig.MODEL_ORIGINAL_TEST_MESSAGES_CSV_ESPAÑOL_DATA_PATH;

        try {
            ResourcesHandler handler = new ResourcesHandler();
            List<String[]> rows = handler.loadQuotedOrPlainLabeledTxtFileAsMessages(relativeOriginalTestLabeledTextPath);

            /*
            System.out.println("\n=== Mensajes extraídos del archivo ===\n");
            int count = 1;
            for (String[] row : rows) {
                System.out.println("Mensaje " + count + ": " + row[0]);
                System.out.println("Label: " + row[1] + "\n");
                count++;
            }
            */
            System.out.println("=======================================");
            System.out.println("TOTAL DE MENSAJES EXTRAÍDOS: " + rows.size());
            System.out.println("=======================================");

        } catch (Exception e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
