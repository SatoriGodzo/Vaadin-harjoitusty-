package com.example.views;

import com.example.data.User;
import com.example.data.UserRepository;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("My Profile")
@PermitAll
public class ProfileView extends VerticalLayout implements AfterNavigationObserver {

    private final AuthenticationContext authContext;
    private final UserRepository userRepository;
    private final Image avatar = new Image();

    public ProfileView(AuthenticationContext authContext, UserRepository userRepository) {
        this.authContext = authContext;
        this.userRepository = userRepository;

        setAlignItems(Alignment.CENTER);

        avatar.setWidth("200px");
        avatar.setHeight("200px");
        avatar.getStyle().set("border-radius", "50%");
        avatar.getStyle().set("object-fit", "cover");
        avatar.getStyle().set("border", "2px solid var(--lumo-contrast-20pct)");

        refreshAvatar();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setAutoUpload(true);

        upload.addSucceededListener(event -> {
            try {
                byte[] bytes = buffer.getInputStream().readAllBytes();
                authContext.getAuthenticatedUser(UserDetails.class).ifPresent(details -> {
                    Optional<User> userMaybe = userRepository.findByUsername(details.getUsername());
                    userMaybe.ifPresent(user -> {
                        user.setProfilePicture(bytes);
                        userRepository.save(user);

                        //Uudelleenlastauksen sivun parametri URL
                        UI.getCurrent().getPage().executeJs("window.location.href = 'profile?saved=true';");
                    });
                });
            } catch (IOException e) {
                Notification.show("Error reading the file!");
            }
        });

        add(avatar, upload);
    }

    // Tämä menetelmä käynnistyy aina, kun sivu ladataan
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Tarkistaa, jos siellä on "tallennettu" - parametri on URL-osoite
        if (event.getLocation().getQueryParameters().getParameters().containsKey("saved")) {
            Notification.show("The avatar has been updated successfully!", 3000, Notification.Position.BOTTOM_START);

            //Valinnainen: poistamme roskat URL-osoitteesta, jotta ilmoitus ei ponnahda uudelleen esiin tavallisella F5: llä
            UI.getCurrent().getPage().executeJs("window.history.replaceState({}, '', 'profile');");
        }
    }

    private void refreshAvatar() {
        authContext.getAuthenticatedUser(UserDetails.class).ifPresent(details -> {
            userRepository.findByUsername(details.getUsername()).ifPresent(user -> {
                if (user.getProfilePicture() != null) {
                    StreamResource resource = new StreamResource("profile-pic.png",
                            () -> new ByteArrayInputStream(user.getProfilePicture()));
                    avatar.setSrc(resource);
                } else {
                    avatar.setSrc("https://via.placeholder.com/200?text=No+Photo");
                }
            });
        });
    }
}