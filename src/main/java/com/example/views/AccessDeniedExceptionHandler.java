package com.example.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AccessDeniedException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import jakarta.servlet.http.HttpServletResponse;

// ВАЖНО: Добавляем @Tag и наследуем Div, иначе Vaadin его не видит как обработчик!
@Tag(Tag.DIV)
public class AccessDeniedExceptionHandler extends Div implements HasErrorParameter<AccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<AccessDeniedException> parameter) {
        // Лог в консоль, чтобы ты увидел, что мы сюда попали
        System.out.println("--- ПЕРЕХВАТ 403 СРАБОТАЛ ---");

        // Перенаправляем на твой роут "access-denied"
        event.rerouteTo("access-denied");

        return HttpServletResponse.SC_FORBIDDEN;
    }
}