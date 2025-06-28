package com.ml.spam.classifierTestMain;

import com.ml.spam.service.ClassifierService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClassifierTestMain {
    public static void main(String[] args) {
        try {
            ClassifierService service = new ClassifierService();

            String remitente = "promo@banco-falso.com";
            String asunto = "OFERTA URGENTE";
            String mensaje = "Compra compra compra compra";
            String texto = asunto + " " + mensaje;

            // Mostrar tokens generados
            try {
                String pythonScript = "spam/src/main/resources/sentencepiece-tokenizer/tokenizar_mensaje_individual.py";
                String modelPath = "messages_spamham_tokenizer.model";

                ProcessBuilder pb = new ProcessBuilder("python", pythonScript, modelPath, texto);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                int exitCode = process.waitFor();

                System.out.println("Tokens generados: " + (line != null ? line : "(ninguno)"));

            } catch (Exception ex) {
                System.out.println("[Error] No se pudieron mostrar los tokens.");
            }

            String resultado = service.classifyEmail(remitente, asunto, mensaje);
            System.out.println("Resultado de clasificación: " + resultado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
