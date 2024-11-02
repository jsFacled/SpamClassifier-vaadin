package com.ml.spam.vaadin.components;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ClassifierResult extends Dialog {

    private final NativeLabel messageLabel;

    public ClassifierResult() {
        // Inicializamos el Label para mostrar el mensaje
        messageLabel = new NativeLabel();
        messageLabel.getStyle().set("font-size", "16px");

        // Botón para cerrar el diálogo
        Button closeButton = new Button("Cerrar", event -> close());

        // Añadimos el mensaje y el botón al layout del diálogo
        VerticalLayout layout = new VerticalLayout(messageLabel, closeButton);
        layout.setSpacing(true);
        add(layout);
    }

    // Método para actualizar el mensaje y mostrar el diálogo
    public void showMessage(String message) {
        messageLabel.setText(message);
        open();
    }
}