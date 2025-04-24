package com.ml.spam.dictionary.stageMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.handlers.ResourcesHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountMessagesFromTripleQuotesMain {

    private static final String inputTxtFilePath = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\correos-spam-fac.txt"; // Cambiá según tu archivo
    private static final String inputTxtFilePath1 = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\mensajes_pruebas_triple_cuotes.txt"; // Cambiá según tu archivo
    private static final String inputTxtFilePath2 = "F:\\JAVA GENERAL\\MACHINE LEARNING JAVA\\Código-ejemplos-intellij\\Clasificador Spam\\SpamClassifier-vaadin\\spam\\src\\main\\resources\\static\\datasets\\mensajes_pruebas_triple_cuotes2.txt"; // Cambiá según tu archivo
    private static final String label = "spam"; // o "ham"

    public static void main(String[] args) throws IOException {

        String content = Files.readString(Paths.get(inputTxtFilePath));

        List<String> mensajes = extractMessages(content);

        System.out.println("Cantidad de mensajes: " + mensajes.size());

        //Mostrar el contenido completo de cada mensaje
    /*   for (int i = 0; i < mensajes.size(); i++) {
            System.out.println("MENSAJE #" + (i + 1) + ":");
            System.out.println(mensajes.get(i));
            System.out.println("------------------------");
        }
    */
    }

    // Método modular para extraer todos los mensajes como lista
    public static List<String> extractMessages(String content) {
        List<String> messages = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?m)^[\"“”]{3}\\s*$([\\s\\S]*?)(?=^[\"“”]{3}\\s*$|\\z)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String message = matcher.group(1).trim();
            if (!message.isEmpty()) {
                messages.add(message);
            }
        }
        return messages;

    }//end main

}
