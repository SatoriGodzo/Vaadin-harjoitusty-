package com.example.views;

import com.example.data.Role;
import com.example.data.User;
import com.example.data.UserRepository;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("User Management")
@RolesAllowed("ADMIN") //vain Admin
public class UserManagementView extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Grid<User> grid = new Grid<>(User.class, false);

    public UserManagementView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        setSizeFull();

        add(new H2("Access control (Admin Panel)"));

        // --- ФОРМА СОЗДАНИЯ НОВОГО ЮЗЕРА ---
        TextField usernameField = new TextField("Login");
        PasswordField passwordField = new PasswordField("Password");
        MultiSelectComboBox<Role> rolesCombo = new MultiSelectComboBox<>("Roles");
        rolesCombo.setItems(Role.values());

        Button addBtn = new Button("Create a user", e -> {
            if (usernameField.isEmpty() || passwordField.isEmpty() || rolesCombo.isEmpty()) {
                Notification.show("Fill in all the fields!");
                return;
            }

            User newUser = new User();
            newUser.setUsername(usernameField.getValue());
            // Kohta 1: Hash salasana ennen tallentamista
            newUser.setHashedValue(passwordEncoder.encode(passwordField.getValue()));
            newUser.setRoles(rolesCombo.getValue());

            userRepository.save(newUser);
            Notification.show("The user has been created!");

            // Kenttien tyhjentäminen ja taulukon päivittäminen
            usernameField.clear();
            passwordField.clear();
            rolesCombo.clear();
            updateGrid();
        });
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout createForm = new HorizontalLayout(usernameField, passwordField, rolesCombo, addBtn);
        createForm.setAlignItems(Alignment.BASELINE);
        add(createForm);

        //--- KÄYTTÄJÄN TAULUKKO ---
        grid.addColumn(User::getUsername).setHeader("login").setAutoWidth(true);

        // Sarake roolien muokkaamista varten suoraan taulukossa
        grid.addComponentColumn(user -> {
            MultiSelectComboBox<Role> editRoles = new MultiSelectComboBox<>();
            editRoles.setItems(Role.values());
            editRoles.setValue(user.getRoles());
            editRoles.addValueChangeListener(ev -> {
                user.setRoles(ev.getValue());
                userRepository.save(user);
                Notification.show("Roles updated for " + user.getUsername());
            });
            return editRoles;
        }).setHeader("Change Roles").setAutoWidth(true);

        //Poista-painike
        grid.addComponentColumn(user -> {
            Button delBtn = new Button("Remove", click -> {
                userRepository.delete(user);
                updateGrid();
            });
            delBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return delBtn;
        });

        add(grid);
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(userRepository.findAll());
    }
}