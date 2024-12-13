package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.datasetProcessor.MessageProcessor;

import java.util.ArrayList;
import java.util.List;

public class MessageProcessorTest {

    public static void main(String[] args) {
        // Simulación de datos crudos (rawRows) en formato [mensaje, etiqueta]
        List<String[]> rawRows = new ArrayList<>();
        rawRows.add(new String[]{"¡Gana un premio ahora!", "spam"});
        rawRows.add(new String[]{"Hola, ¿cómo estás?", "ham"});
        rawRows.add(new String[]{"Oferta exclusiva 1000 solo hoy", "spam"});
        rawRows.add(new String[]{"Te llamo luego", "ham"});
        rawRows.add(new String[]{"Mensaje inválido solo texto", ""}); // Fila inválida
        rawRows.add(new String[]{"", "ham"}); // Fila inválida
        rawRows.add(new String[]{"¡Oferta limitada $100!", "spam"});

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
