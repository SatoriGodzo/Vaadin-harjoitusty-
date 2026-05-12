package com.example.base.ui;

import com.example.data.User;
import com.example.data.UserRepository;
import com.example.views.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Layout
@CssImport("./styles.css")
@AnonymousAllowed
public final class MainLayout extends AppLayout {

    private final AuthenticationContext authContext;
    private final UserRepository userRepository; // Добавили репозиторий

    public MainLayout(AuthenticationContext authContext, UserRepository userRepository) {
        this.authContext = authContext;
        this.userRepository = userRepository; // Инициализируем

        setPrimarySection(Section.DRAWER);

        addToNavbar(createApplicationHeader());

        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);

        drawerContent.add(createApplicationDrawer());
        drawerContent.add(createApplicationFooter());

        addToDrawer(drawerContent);
    }

    private Component createApplicationHeader() {
        var drawerToggle = new DrawerToggle();

        var icon = VaadinIcon.CLUSTER.create();
        icon.getStyle().set("margin-left", "var(--lumo-space-m)");

        var appName = new Span("Company Manager");
        appName.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY
        );

        var left = new HorizontalLayout(drawerToggle, icon, appName);
        left.setAlignItems(FlexComponent.Alignment.CENTER);

        // ИСПРАВЛЕННАЯ ЛОГИКА: Безопасное получение имени пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser"))
                ? auth.getName()
                : "Guest";

        // ЛОГИКА АВАТАРКИ
        Avatar userAvatar = new Avatar(currentUsername);
        if (!currentUsername.equals("Guest")) {
            Optional<User> userMaybe = userRepository.findByUsername(currentUsername);
            userMaybe.ifPresent(u -> {
                if (u.getProfilePicture() != null) {
                    StreamResource resource = new StreamResource("avatar",
                            () -> new ByteArrayInputStream(u.getProfilePicture()));
                    userAvatar.setImageResource(resource);
                }
            });
        }
        userAvatar.getStyle().set("margin-right", "10px");

        var userName = new Span("User: " + currentUsername);
        userName.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        var logoutButton = new Button("Logout", e -> authContext.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        // Кнопка выхода видна только авторизованным
        logoutButton.setVisible(auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser"));

        // Добавили аватарку в правую часть
        var right = new HorizontalLayout(userAvatar, userName, logoutButton);
        right.setAlignItems(FlexComponent.Alignment.CENTER);
        right.setSpacing(true);
        right.getStyle().set("margin-right", "var(--lumo-space-m)");

        var headerLayout = new HorizontalLayout(left, right);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        var header = new Header(headerLayout);
        header.getStyle().set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        return header;
    }

    private Component createApplicationDrawer() {
        var scroller = new Scroller(createSideNav());
        scroller.setWidthFull();
        scroller.getStyle().set("flex", "1");
        return scroller;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();

        nav.addItem(new SideNavItem(
                "Home",
                HomeView.class,
                VaadinIcon.HOME.create()
        ));

        // Добавляем Профиль в меню
        nav.addItem(new SideNavItem(
                "My Profile",
                ProfileView.class,
                VaadinIcon.USER.create()
        ));

        nav.addItem(new SideNavItem(
                "Advanced Search",
                AdvancedSearchView.class,
                VaadinIcon.SEARCH.create()
        ));

        nav.addItem(new SideNavItem(
                "Employees",
                EmployeeView.class,
                VaadinIcon.USERS.create()
        ));

        nav.addItem(new SideNavItem(
                "Departments",
                DepartmentView.class,
                VaadinIcon.BUILDING.create()
        ));

        nav.addItem(new SideNavItem(
                "Projects",
                ProjectView.class,
                VaadinIcon.BRIEFCASE.create()
        ));

        nav.addItem(new SideNavItem(
                "Manage Users",
                UserManagementView.class,
                VaadinIcon.COG.create()
        ));

        nav.addItem(new SideNavItem(
                "Access Cards",
                AccessCardView.class,
                VaadinIcon.KEY.create()
        ));

        return nav;
    }

    private Component createApplicationFooter() {
        var appTitle = new Span("Company Manager");
        appTitle.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-primary-text-color)");

        var author = new Span("Created by: Aleksei Egorov");
        author.getStyle()
                .set("font-size", "var(--lumo-font-size-xxs)")
                .set("font-variant", "small-caps")
                .set("color", "var(--lumo-secondary-text-color)");

        var copyright = new Span("© 2026 All Rights Reserved");
        copyright.addClassNames(LumoUtility.FontSize.XXSMALL, LumoUtility.TextColor.TERTIARY);

        var helpLink = new Anchor("mailto:support@company.com", "Support");
        helpLink.getStyle().set("font-size", "var(--lumo-font-size-xxs)");

        var footerLayout = new VerticalLayout(appTitle, author, copyright, helpLink);
        footerLayout.setSpacing(false);
        footerLayout.setPadding(true);
        footerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        var footer = new Footer(footerLayout);
        footer.setWidthFull();
        footer.getStyle()
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("background-color", "var(--lumo-contrast-5pct)");

        return footer;
    }
}