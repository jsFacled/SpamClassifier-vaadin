package com.ml.spam.datasetProcessor.stageMain;

import com.ml.spam.datasetProcessor.utils.DatasetStatsUtil;
import com.ml.spam.datasetProcessor.utils.DuplicateMessageChecker;
import com.ml.spam.dictionary.models.MessageLabel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Muestra estadísticas básicas de un archivo de mensajes.
 */
public class DatasetStatsMain {

    public static void main(String[] args) throws Exception {
        String inputArg = args.length > 0 ? args[0] : "joined_messages_labels_normalized_unique.txt";
        String formatArg = args.length > 1 ? args[1] : "LINE_BY_LINE";

        Path input = Paths.get(inputArg);
        DuplicateMessageChecker.InputFormat format = DuplicateMessageChecker.InputFormat.valueOf(formatArg);

        long rows = DatasetStatsUtil.countRows(input, format);
        Map<MessageLabel, Long> labels = DatasetStatsUtil.countLabels(input, format);
        int dups = DatasetStatsUtil.countDuplicates(input, format);

        System.out.println("Total de filas: " + rows);
        for (Map.Entry<MessageLabel, Long> e : labels.entrySet()) {
            System.out.println(e.getKey().getKey() + ": " + e.getValue());
        }
        System.out.println("Duplicados: " + dups);
    }
}