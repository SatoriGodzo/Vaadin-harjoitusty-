package com.example.views;

import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route(value = "access-denied", layout = MainLayout.class)
@PageTitle("Access Denied")
@PermitAll
public class AccessDeniedView extends VerticalLayout {

    public AccessDeniedView() {
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMinHeight("70vh");

        // Kilpi-tai kieltokuvake
        var icon = VaadinIcon.SHIELD.create();
        icon.getStyle().set("font-size", "100px");
        icon.getStyle().set("color", "var(--lumo-error-color)");
        icon.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        H1 errorTitle = new H1("403 - Access is restricted");
        errorTitle.addClassNames(LumoUtility.TextColor.ERROR, LumoUtility.FontSize.XXLARGE);

        Span message = new Span("Oops! It seems you don't have the keys to this door. Сontact the administrator");
        message.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);

        Button backHome = new Button("Go back to the main page", VaadinIcon.HOME.create(),
                e -> getUI().ifPresent(ui -> ui.navigate("")));
        backHome.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        backHome.addClassNames(LumoUtility.Margin.Top.LARGE);

        add(icon, errorTitle, message, backHome);
    }
}