package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.handlers.ResourcesHandler;

import java.util.List;

/**
 * Programa principal para mostrar mensajes extraídos de un archivo con triples comillas.
 */
public class ShowTripleQuotedMessagesMain {

    public static void main(String[] args) {
       String relativeOriginalTriplecomillasPath = args.length > 0 ? args[0] : FilePathsConfig.MODEL_ORIGINAL_CORREOS_SPAM_FAC_TXT_PATH;
        String relativeIATriplecomillasPath = args.length > 0 ? args[0] : FilePathsConfig.IA_GENERATED_TRIPLECUOTES_SPAM_PATH;

        //Para realizar pruebas
        String relativePathTrial = args.length > 0 ? args[0] : FilePathsConfig.TRIAL_MESSAGES_TIPLECUOTES_Y_SIMPLECUOTE_PATH;

        try {
            ResourcesHandler handler = new ResourcesHandler();
            List<String> mensajes = handler.loadTripleQuotedTxtFileAsMessages(relativeOriginalTriplecomillasPath);

            System.out.println("\n=== Mensajes extraídos del archivo ===\n");
            int count = 1;
            for (String mensaje : mensajes) {
               // System.out.println("mensaje " + count + ": " + mensaje + "\n");
                count++;
            }

            System.out.println("\n=======================================");
            System.out.println("TOTAL DE MENSAJES EXTRAÍDOS: " + mensajes.size());
            System.out.println("=======================================");


        } catch (Exception e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
