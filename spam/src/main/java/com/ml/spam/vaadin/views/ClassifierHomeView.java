package com.ml.spam.vaadin.views;

import com.ml.spam.service.ClassifierService;
import com.ml.spam.vaadin.components.ClassifierResult;
import com.ml.spam.vaadin.components.EmailForm;
import com.ml.spam.vaadin.components.HeaderGlobalApp;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class ClassifierHomeView extends VerticalLayout {

    private final ClassifierService classifierService;
    private final EmailForm emailForm;
    private final ClassifierResult classifierResult;

    @Autowired
    public ClassifierHomeView(ClassifierService classifierService) {
        this.classifierService = classifierService;

        // Inicializar los componentes
        HeaderGlobalApp header = new HeaderGlobalApp();
        classifierResult = new ClassifierResult();
        emailForm = new EmailForm();

        // Agregar el listener al botón enviar
        emailForm.getEnviarButton().addClickListener(event -> classifyEmail());

        // Agregar componentes al layout
        add(header, emailForm, classifierResult);
    }

    private void classifyEmail() {
        emailForm.setEnabledEnviarButton(false);

        String origen = emailForm.getOrigen();
        String titulo = emailForm.getTitulo();
        String texto = emailForm.getTexto();

        String mensajeClasificacion = classifierService.processEmail(origen, titulo, texto);

        classifierResult.showMessage(mensajeClasificacion);
        emailForm.setEnabledEnviarButton(true);
    }
}
