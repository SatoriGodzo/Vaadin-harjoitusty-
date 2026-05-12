package com.example.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | Company Manager")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();
    private final VerticalLayout successBox = new VerticalLayout();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        // Блок уведомления об успехе
        Span successText = new Span("✔ Registration successful! Please log in.");
        successBox.add(successText);
        successBox.setVisible(false);
        successBox.setWidth("360px");
        successBox.setAlignItems(Alignment.CENTER);

        successBox.getStyle().set("background-color", "#e7f4e9");
        successBox.getStyle().set("border", "2px solid #28a745");
        successBox.getStyle().set("border-radius", "8px");
        successBox.getStyle().set("color", "#1e4620");
        successBox.getStyle().set("font-weight", "bold");
        successBox.getStyle().set("padding", "10px");
        successBox.getStyle().set("margin-bottom", "20px");

        // ССЫЛКА НА РЕГИСТРАЦИЮ
        Span text = new Span("Don't have an account? ");
        Anchor registerLink = new Anchor("registration", "Register here");

        // --- МЯГКИЙ И ЗАМЕТНЫЙ СТИЛЬ ---
        registerLink.getStyle().set("color", "#006af5"); // Красивый синий (фирменный Vaadin)
        registerLink.getStyle().set("font-weight", "600"); // Умеренно жирный
        registerLink.getStyle().set("text-decoration", "none"); // Убираем постоянную линию
        registerLink.getStyle().set("font-size", "1.1em"); // Оставляем чуть крупнее
// Добавим эффект при наведении, чтобы было понятно, что это ссылка
        registerLink.getElement().getStyle().set("cursor", "pointer");
// -------------------------------

        HorizontalLayout registrationLayout = new HorizontalLayout(text, registerLink);
        registrationLayout.setSpacing(true); // Добавим зазор между текстом и ссылкой
        registrationLayout.setAlignItems(Alignment.BASELINE);

        add(
                new H1("Company Manager"),
                successBox,
                login,
                registrationLayout
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("registered")) {
            successBox.setVisible(true);
        }

        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}