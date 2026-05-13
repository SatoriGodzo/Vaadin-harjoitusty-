package com.example.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private final LoginForm login = new LoginForm();
    private final VerticalLayout successBox = new VerticalLayout();

    // Поля для ссылок и текстов, чтобы менять их динамически
    private final Span successText = new Span();
    private final Span noAccountText = new Span();
    private final Anchor registerLink = new Anchor("registration", "");

    @Override
    public String getPageTitle() {
        return getTranslation("login.page.title");
    }

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        // --- LOKALISOINTI MUODOSSA (Käyttäjätunnus, Salasana, jne.) ---
        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle(getTranslation("login.form.title"));
        i18nForm.setUsername(getTranslation("login.form.username"));
        i18nForm.setPassword(getTranslation("login.form.password"));
        i18nForm.setSubmit(getTranslation("login.form.submit"));
        i18nForm.setForgotPassword(getTranslation("login.form.forgot-password"));

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle(getTranslation("login.error.title"));
        i18nErrorMessage.setMessage(getTranslation("login.error.message"));

        login.setI18n(i18n);
        // -----------------------------------------------------

        // Onnistumisilmoitusten Lohko
        successText.setText(getTranslation("login.success.registration"));
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

        // LINKKI ILMOITTAUTUMISEEN
        noAccountText.setText(getTranslation("login.no-account"));
        registerLink.setText(getTranslation("login.register-here"));

        registerLink.getStyle().set("color", "#006af5");
        registerLink.getStyle().set("font-weight", "600");
        registerLink.getStyle().set("text-decoration", "none");
        registerLink.getStyle().set("font-size", "1.1em");
        registerLink.getElement().getStyle().set("cursor", "pointer");

        HorizontalLayout registrationLayout = new HorizontalLayout(noAccountText, registerLink);
        registrationLayout.setSpacing(true);
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