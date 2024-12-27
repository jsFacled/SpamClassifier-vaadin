package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.datasetProcessor.MessageProcessor;

import java.util.ArrayList;
import java.util.List;

public class MessageProcessorTest {

    public static void main(String[] args) {
        // Simulación de datos crudos (rawRows) en formato [mensaje, etiqueta]
        List<String[]> rawRows = new ArrayList<>();
        rawRows.add(new String[]{"Compra 35kg de arroz en oferta! $100 24hs es un buen precio para ahorrar y es urgente! http://promo123.com \uD83D\uDE0A cómpralo ya yá", "spam"});

        // Procesar las filas crudas para convertirlas en WordData
        try {
            List<List<WordData>> processedData = MessageProcessor.processToWordData(rawRows);

            // Mostrar los resultados procesados
            System.out.println("Resultados del procesamiento:");
            for (List<WordData> wordDataList : processedData) {
                System.out.println("Nuevo mensaje procesado:");
                for (WordData wordData : wordDataList) {
                    System.out.println(wordData);
                }
                System.out.println("--------------------");


            }
            System.out.println("\n ************ Lista completa ************\n");
            System.out.println(processedData);

        } catch (IllegalArgumentException e) {
            System.err.println("Error en la entrada de datos: " + e.getMessage());
        }


    }
}
