package com.example.views;

import com.example.data.Role;
import com.example.data.User;
import com.example.data.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Route("registration")
@PageTitle("Register")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");
    private final Button register = new Button("Register");

    public RegistrationView(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        register.addClickListener(e -> registerUser());

        FormLayout form = new FormLayout(
                new H2("Create account"),
                username,
                password,
                confirmPassword,
                register
        );

        form.setMaxWidth("400px");

        add(form);
    }

    private void registerUser() {

        if (username.isEmpty() || password.isEmpty()) {
            Notification.show("Fill all fields");
            return;
        }

        if (!password.getValue().equals(confirmPassword.getValue())) {
            Notification.show("Passwords do not match");
            return;
        }

        User user = new User();
        user.setUsername(username.getValue());
        user.setHashedValue(passwordEncoder.encode(password.getValue()));
        user.setRoles(Collections.singleton(Role.USER));

        userRepository.save(user);

        Notification.show("Account created");

        getUI().ifPresent(ui -> ui.navigate("login"));
    }
}