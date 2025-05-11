package com.ml.spam.datasetProcessor.utils;

import com.ml.spam.datasetProcessor.models.DatasetColumnName;

import java.util.ArrayList;
import java.util.List;

public class DatasetHeaderBuilder {

    public static List<String> generateHeader(List<String> strongSpamWords) {
        List<String> header = new ArrayList<>();

        // --- Features dinámicos por palabra strongSpamWord ---
        for (String word : strongSpamWords) {
            header.add(DatasetColumnName.FREQ.get() + word);
            header.add(DatasetColumnName.RELATIVE_FREQ_NORM.get() + word);
            header.add(DatasetColumnName.WEIGHT.get() + word);
            header.add(DatasetColumnName.POLARITY.get() + word);
        }

        // --- Features globales fijos ---
        for (DatasetColumnName col : DatasetColumnName.values()) {
            if (!col.name().equals("FREQ")
                    && !col.name().equals("RELATIVE_FREQ_NORM")
                    && !col.name().equals("WEIGHT")
                    && !col.name().equals("POLARITY")) {
                header.add(col.get());
            }
        }

        return header;
    }
}
