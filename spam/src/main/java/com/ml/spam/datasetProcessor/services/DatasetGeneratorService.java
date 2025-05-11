package com.ml.spam.datasetProcessor.services;

import com.ml.spam.datasetProcessor.MessageProcessor;
import com.ml.spam.datasetProcessor.models.DatasetRow;
import com.ml.spam.datasetProcessor.utils.DatasetFeatureCalculator;
import com.ml.spam.datasetProcessor.utils.DatasetExporter;
import com.ml.spam.datasetProcessor.utils.DatasetHeaderBuilder;
import com.ml.spam.datasetProcessor.metadata.LexemeWordMetadata;
import com.ml.spam.datasetProcessor.utils.DatasetRowBuilder;
import com.ml.spam.dictionary.models.SpamDictionary;
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

    public void generateDatasetFromCsvMessages(
            String catWordsPath,
            String lexemePath,
            String metadataPath,
            String csvMessagesFilePath,
            String outputPath) throws IOException {

        // 1. Inicializar el diccionario global
        dictionaryService.initializeDictionaryFromJson(catWordsPath, lexemePath, metadataPath);

        // 2. Cargar metadata auxiliar de palabras por lexema
        LexemeWordMetadata lexemeMetadata = new LexemeWordMetadata("static/lexemes/lexeme_words_detailed.json");

        // 3. Cargar el corpus de mensajes desde el CSV
        List<String[]> rawRows = resourcesHandler.loadCsvFile(csvMessagesFilePath);

        // 4. Filtrar filas válidas
        List<String[]> validRows = rawRows.stream()
                .filter(TextUtils::isRawRow)
                .toList();

        if (validRows.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron filas válidas en el archivo CSV.");
        }

        // 5. Procesar los mensajes a listas de WordData
        Map<CharSize, Map<String, Set<String>>> lexemeRepository = dictionaryService.getDictionary().getLexemesRepository();
        List<List<WordData>> processedWordData = MessageProcessor.processToWordData(validRows, lexemeRepository);

        // 6. Calcular las filas del dataset
        DatasetRowBuilder rowBuilder = new DatasetRowBuilder(dictionary, lexemeMetadata, schema);
        List<DatasetRow> datasetRows = new ArrayList<>();

        for (int i = 0; i < processedWordData.size(); i++) {
            List<WordData> messageTokens = processedWordData.get(i);
            String label = validRows.get(i)[1];
            datasetRows.add(rowBuilder.buildRow(messageTokens, label));
        }


        // 7. Generar el header
        List<String> header = DatasetHeaderBuilder.generateHeader(dictionaryService);

        // 8. Exportar a CSV
        DatasetExporter.exportDataset(datasetRows, header, outputPath);

        System.out.println("[INFO] Dataset generado correctamente en: " + outputPath);
    }
}
