package com.ml.spam.vaadin.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class EmailForm extends VerticalLayout {

    private final TextField tituloField;
    private final TextArea textoArea;
    private final NumberField thresholdField;
    private final Button enviarButton;

    public EmailForm() {
        // Título de instrucciones
        H2 instructions = new H2("Llena el formulario con los datos del email que deseas clasificar.");

        // Campos de entrada para el correo electrónico
        tituloField = new TextField("Título");
        textoArea = new TextArea("Texto del correo");

        // Campo numérico para el umbral de spam
        thresholdField = new NumberField("Umbral de spam");
        thresholdField.setValue(0.5);       // Valor inicial de 0.5
        thresholdField.setMin(0.0);         // Valor mínimo
        thresholdField.setMax(1.0);         // Valor máximo
        thresholdField.setStep(0.1);        // Incremento en pasos de 0.1

        // Botón para enviar
        enviarButton = new Button("Enviar");

        // Agregar componentes al layout
        add(instructions, tituloField, textoArea, thresholdField, enviarButton);
    }

    public Button getEnviarButton() {
        return enviarButton;
    }

    public String getTitulo() {
        return tituloField.getValue();
    }

    public String getTexto() {
        return textoArea.getValue();
    }

    public double getThreshold() {
        return thresholdField.getValue();
    }

    public void setEnabledEnviarButton(boolean enabled) {
        enviarButton.setEnabled(enabled);
    }
}
