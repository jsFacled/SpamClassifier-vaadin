package com.ml.spam.vaadin.components;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HeaderGlobalApp extends HorizontalLayout {

    public HeaderGlobalApp() {
        // Configuración del diseño general del header
        setWidthFull();
        setPadding(true);
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        addClassName("header-global-app");

        // Logo
        Image logo = new Image("images/logo.png", "Logo de la aplicación");
        logo.setHeight("40px");

        // Contenedor del logo
        Div logoContainer = new Div(logo);

        // Enlace de la app
        Anchor appLink = new Anchor("/home", "SpamApp");
        appLink.addClassName("app-link");

        // Texto centrado
        Span centerText = new Span("Java Machine Learning");
        centerText.addClassName("center-text");

        // Avatar de usuario
        Avatar userAvatar = new Avatar("Usuario");
        userAvatar.addClassName("user-avatar");

        // Secciones del header
        HorizontalLayout leftSection = new HorizontalLayout(logoContainer, appLink);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout centerSection = new HorizontalLayout(centerText);
        centerSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centerSection.setWidthFull();

        HorizontalLayout rightSection = new HorizontalLayout(userAvatar);
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);

        // Añadir secciones al header
        add(leftSection, centerSection, rightSection);
    }
}
