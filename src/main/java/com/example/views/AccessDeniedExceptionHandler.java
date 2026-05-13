package com.example.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AccessDeniedException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import jakarta.servlet.http.HttpServletResponse;

// Tärkeää: Lisää @ - tunniste ja peri Div, muuten Vaadin ei näe sitä käsittelijänä!
@Tag(Tag.DIV)
public class AccessDeniedExceptionHandler extends Div implements HasErrorParameter<AccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<AccessDeniedException> parameter) {
        // Kirjaudu sisään konsoliin, jotta näet, että olemme täällä.
        System.out.println("--- SIEPPAUS 403 ON LAUKAISTU.---");

        // Uudelleenohjaus reitittimeen "access-denied"
        event.rerouteTo("access-denied");

        return HttpServletResponse.SC_FORBIDDEN;
    }
}