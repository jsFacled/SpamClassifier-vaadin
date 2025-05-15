package com.ml.spam.datasetProcessor.services;

import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.datasetProcessor.models.DatasetRow;
import com.ml.spam.datasetProcessor.schema.DatasetSchema;
import com.ml.spam.datasetProcessor.utils.DatasetFeatureCalculator;
import com.ml.spam.datasetProcessor.utils.DatasetExporter;
import com.ml.spam.datasetProcessor.utils.DatasetHeaderBuilder;
import com.ml.spam.datasetProcessor.metadata.LexemeWordMetadata;
import com.ml.spam.datasetProcessor.utils.DatasetRowBuilder;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordCategory;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.models.CharSize;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;
import com.ml.spam.utils.TextUtils;

import java.io.IOException;
import java.util.*;

public class DatasetGeneratorService {

    private final SpamDictionaryService dictionaryService;
    private final ResourcesHandler resourcesHandler;

    public DatasetGeneratorService() {
        this.dictionaryService = new SpamDictionaryService();
        this.resourcesHandler = new ResourcesHandler();
    }

    public void generateDatasetFromCorpus(
            String catWordsPath,
            String lexemePath,
            String metadataPath,
            String corpusPath,
            String format,         // "csv" o "txt"
            String labelIfTxt,     // "spam" o "ham" (solo si format == "txt")
            String outputPath
    ) throws IOException {

        // 1. Inicializar el diccionario
        dictionaryService.initializeDictionaryFromJson(catWordsPath, lexemePath, metadataPath);

        // 2. Cargar metadata auxiliar
        LexemeWordMetadata lexemeMetadata = new LexemeWordMetadata("static/lexemes/lexeme_words_detailed.json");

        // 3. Cargar corpus desde CSV o TXT con etiqueta externa
        List<String[]> validRows = resourcesHandler.loadCorpusRows(corpusPath, format, labelIfTxt);

        if (validRows.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron filas válidas en el corpus.");
        }

        // 4. Procesar mensajes a WordData
        Map<CharSize, Map<String, Set<String>>> lexemeRepository = dictionaryService.getDictionary().getLexemesRepository();
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(validRows, lexemeRepository);

        // 5. Construir filas del dataset
        var strongWords = dictionaryService.getCategorizedWords()
                .get(WordCategory.STRONG_SPAM_WORD)
                .keySet();
        var schema = new DatasetSchema(strongWords);
        var dictionary = dictionaryService.getDictionary();

        var rowBuilder = new DatasetRowBuilder(dictionary, lexemeMetadata, schema);
        List<DatasetRow> datasetRows = new ArrayList<>();

        for (int i = 0; i < processedWordData.size(); i++) {
            List<WordData> messageTokens = processedWordData.get(i);
            String label = validRows.get(i)[1];
            datasetRows.add(rowBuilder.buildRow(messageTokens, label));
        }

        // 6. Generar header
        List<String> header = DatasetHeaderBuilder.generateHeader(schema.getStrongSpamWords());

        // 7. Exportar a CSV
     //   DatasetExporter.exportDataset(datasetRows, header, outputPath);

        System.out.println("[INFO] Dataset generado correctamente en: " + outputPath);
    }

}
