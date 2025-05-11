package com.ml.spam.datasetProcessor.utils;

public class DatasetExporter {

    /*

    public void exportNewWords(SpamDictionary dictionary, String filePath) throws IOException {
        JSONObject jsonNewWords = new JSONObject();
        dictionary.getNewWords().forEach((word, frequency) -> {
            JSONObject freqData = new JSONObject();
            freqData.put("spamFrequency", frequency.getSpamFrequency());
            freqData.put("hamFrequency", frequency.getHamFrequency());
            jsonNewWords.put(word, freqData);
        });

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonNewWords.toString(4));
        }
    }

     */
}
