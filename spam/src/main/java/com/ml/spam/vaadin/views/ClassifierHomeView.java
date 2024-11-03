package com.ml.spam.vaadin.views;

import com.ml.spam.service.ClassifierService;
import com.ml.spam.vaadin.components.ClassifierResult;
import com.ml.spam.vaadin.components.EmailForm;
import com.ml.spam.vaadin.components.HeaderGlobalApp;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class ClassifierHomeView extends VerticalLayout {

    private final ClassifierService classifierService;
    private final EmailForm emailForm;
    private final ClassifierResult classifierResult;
    private final ComboBox<String> optionSelector;

    @Autowired
    public ClassifierHomeView(ClassifierService classifierService) {
        this.classifierService = classifierService;

        // Inicializar los componentes
        HeaderGlobalApp header = new HeaderGlobalApp();
        classifierResult = new ClassifierResult();
        emailForm = new EmailForm();

        // Configurar ComboBox para seleccionar el tipo de resultado
        optionSelector = new ComboBox<>("Seleccione la opción de clasificación");
        optionSelector.setItems("Clasificación", "Probabilidad de Spam");
        optionSelector.setValue("Clasificación"); // Valor predeterminado

        // Agregar listener al botón enviar
        emailForm.getEnviarButton().addClickListener(event -> classifyEmail());

        // Agregar componentes al layout
        add(header, optionSelector, emailForm, classifierResult);
    }

    private void classifyEmail() {
        emailForm.setEnabledEnviarButton(false);

        // Obtener datos del formulario

        String titulo = emailForm.getTitulo();
        String texto = emailForm.getTexto();

        // Determinar acción según la opción seleccionada
        String resultado;
        if ("Clasificación".equals(optionSelector.getValue())) {
            // Clasificación directa
            resultado = classifierService.classifyEmail(titulo, texto);
        } else {
            // Mostrar probabilidad de spam como porcentaje
            float probabilidad = classifierService.getSpamProbability(titulo, texto);
            resultado = String.format("Probabilidad de ser spam: %.2f%%", probabilidad * 100);
        }

        // Mostrar resultado en ClassifierResult
        classifierResult.showMessage(resultado);
        emailForm.setEnabledEnviarButton(true);
    }
}
