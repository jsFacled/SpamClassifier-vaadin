package com.ml.spam.mlmodel;

import javax.visrec.ml.classification.BinaryClassifier;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ModelLoaderTest {

    public static void main(String[] args) {
        try {
            InputStream fileIn = ModelLoaderTest.class.getClassLoader().getResourceAsStream("models/feedforward_spam_classifier.ser");
            if (fileIn == null) {
                throw new FileNotFoundException("El archivo feedforward_spam_classifier.ser no se encontró en el classpath.");
            }
            try (ObjectInputStream in = new ObjectInputStream(fileIn)) {
                BinaryClassifier<float[]> classifier = (BinaryClassifier<float[]>) in.readObject();
                System.out.println("Modelo cargado exitosamente.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
