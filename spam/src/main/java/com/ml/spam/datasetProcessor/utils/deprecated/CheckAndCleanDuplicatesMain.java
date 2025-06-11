package com.ml.spam.datasetProcessor.utils.deprecated;

import com.ml.spam.datasetProcessor.utils.DuplicateMessageChecker;

public class CheckAndCleanDuplicatesMain {

    public static void main(String[] args) throws Exception {

        // ==== CONFIGURACIÓN DE ENTRADA Y SALIDA ====

        // Para mensajes línea por línea (ej: mensaje,label)
        String inputPath = "spam/src/main/resources/static/datasets/joined/joined_messages_label.csv";
        String outputPath = "spam/src/main/resources/static/datasets/cleaned/joined_messages_label_cleaned.csv";
        DuplicateMessageChecker.InputFormat format = DuplicateMessageChecker.InputFormat.LINE_BY_LINE;

        // Para triple comillas (descomentar si querés usar este)
        // String inputPath = "spam/src/main/resources/static/datasets/joined/joined_messages_triplecomillas_spam.txt";
        // String outputPath = "spam/src/main/resources/static/datasets/cleaned/joined_messages_triplecomillas_spam_cleaned.txt";
        // DuplicateMessageChecker.InputFormat format = DuplicateMessageChecker.InputFormat.TRIPLE_QUOTED;

        // ==== CONFIG DE LIMPIEZA ====

        DuplicateMessageChecker checker = new DuplicateMessageChecker();
        checker.setIgnoreCase(true);
        checker.setNormalizeWhitespace(true);
        checker.setStripQuotes(true);
        checker.setRemoveLabel(false); // true si querés ignorar el label en la comparación

        // ==== PROCESO ====
        int count = checker.countDuplicates(inputPath, format);
        System.out.println("🔍 Duplicados detectados: " + count);

        checker.removeDuplicates(inputPath, outputPath, format);
    }
}
