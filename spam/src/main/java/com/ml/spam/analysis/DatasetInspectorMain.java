package com.ml.spam.analysis;

public class DatasetInspectorMain {
    public static void main(String[] args) {
        System.setProperty("prism.order", "sw"); // fuerza renderizado por software
        DatasetInspectorUI.main(args); // lanza la interfaz gráfica con botones
    }
}
