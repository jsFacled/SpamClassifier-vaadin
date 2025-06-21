package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.datasetProcessor.utils.FileJoiner;

import java.util.ArrayList;
import java.util.List;

/**
 * Une varios archivos de texto (uno por línea o triple comillas) en un solo archivo.
 */
public class JoinTextFilesMain {

    public static void main(String[] args) throws Exception {
        // Archivos de entrada por defecto
        String[] defaults = {
                FilePathsConfig.MODEL_ORIGINAL_CORREOS_SPAM_FAC_TXT_PATH,
                FilePathsConfig.IA_GENERATED_TRIPLECUOTES_SPAM_PATH

                 };
        String outputArg;
        List<String> inputs = new ArrayList<>();
        if (args.length >= 2) {
            for (int i = 0; i < args.length - 1; i++) {
                inputs.add(args[i]);
            }
            outputArg = args[args.length - 1];
        } else {
            for (String d : defaults) {
                inputs.add(d);
            }
            outputArg =FilePathsConfig.COMBINED_SPAM_TRIPLE_QUOTES_OUTPUT_PATH;
        }

        FileJoiner joiner = new FileJoiner(); // ✅ Instancia
        joiner.joinFiles(inputs, outputArg, false); // ✅ false indica que son archivos de texto, no CSV
        System.out.println("✅ Archivos combinados en: " + outputArg);
    }
}
