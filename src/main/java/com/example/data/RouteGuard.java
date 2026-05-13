package com.example.data;

import com.example.views.AccessDeniedView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringComponent
@UIScope
public class RouteGuard implements BeforeEnterListener {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        Class<?> target = event.getNavigationTarget();

        // ✅ 1.Julkiset sivut (tärkeää — merkinnän, Ei luokan nimen kautta)
        if (target.isAnnotationPresent(AnonymousAllowed.class)) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ❗ 2. anonyymi käyttäjän vahvistus (täydellinen ja oikea)
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken
                || "anonymousUser".equals(auth.getName())) {

            event.rerouteTo("login");
            return;
        }

        // ✅ 3. jos rooleja ei ole, pääsy on sallittu.
        if (!target.isAnnotationPresent(RolesAllowed.class)) {
            return;
        }

        RolesAllowed roles = target.getAnnotation(RolesAllowed.class);

        // ✅ 4.roolin todentaminen
        boolean allowed = auth.getAuthorities().stream()
                .anyMatch(a -> {
                    for (String role : roles.value()) {
                        if (a.getAuthority().equals("ROLE_" + role)
                                || a.getAuthority().equals(role)) {
                            return true;
                        }
                    }
                    return false;
                });

        // ❌ 5. ei pääsyä → 403
        if (!allowed) {
            event.rerouteTo(AccessDeniedView.class);
        }
    }
}