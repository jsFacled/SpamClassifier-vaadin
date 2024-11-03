package com.ml.spam.mlmodel;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.FileIO;

import org.json.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ModelLoaderTest {

    public static void main(String[] args) {
        try {
            // Ruta al archivo en formato JSON
            String filePath = "spam/src/main/resources/models/feedforward_spam_classifier.json";
            File modelFile = new File(filePath);

            // Verificar si el archivo existe y es accesible
            if (!modelFile.exists()) {
                System.err.println("El archivo no existe en la ruta especificada: " + filePath);
                return;
            } else {
                System.out.println("Archivo encontrado en la ruta: " + modelFile.getAbsolutePath());
            }

            // Leer el contenido JSON del archivo como String
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));

            // Convertir el contenido a JSONObject y cargar el modelo desde JSON
            FeedForwardNetwork neuralNet = (FeedForwardNetwork) FileIO.createFromJson(new JSONObject(jsonString));
            System.out.println("Modelo cargado exitosamente desde el archivo .json");

            // Ejemplo de uso del modelo
            System.out.println("Probando la predicción con un ejemplo de entrada...");
            float[] input = new float[57];
            float output = neuralNet.predict(input)[0];
            System.out.println("Resultado de la clasificación: " + output);

        } catch (IOException e) {
            System.err.println("Error de E/S al cargar el modelo: " + e.getMessage());
        } catch (ClassCastException e) {
            System.err.println("Error al convertir el objeto cargado al tipo FeedForwardNetwork: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
        }
    }
}
