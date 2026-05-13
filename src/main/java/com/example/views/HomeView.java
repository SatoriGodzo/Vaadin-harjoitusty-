package com.example.views;

import com.example.data.EmployeeRepository;
import com.example.data.ProjectRepository;
import com.example.data.DepartmentRepository;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
@PermitAll
public class HomeView extends VerticalLayout {

    // Toteutamme tietovarastoja rakentajan kautta saadaksemme todellista tietoa.
    public HomeView(EmployeeRepository employeeRepo,
                    DepartmentRepository deptRepo,
                    ProjectRepository projectRepo) {

        addClassName("home-view"); //  CSS (kojta 3 )

        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMinHeight("80vh");

        // Tärkein onnittelukortti
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                LumoUtility.Background.BASE,        // a. Background
                LumoUtility.TextColor.BODY,         // b. TextColor
                LumoUtility.Padding.XLARGE,         // c. Padding
                LumoUtility.BoxShadow.LARGE,        // d. BoxShadow
                LumoUtility.BorderRadius.LARGE      // e. BorderRadius
        );
        card.setWidth("auto");
        card.setMaxWidth("600px");
        card.setAlignItems(Alignment.CENTER);

        var icon = VaadinIcon.CLUSTER.create();
        icon.addClassName("welcome-icon"); // CSS-animaatio ( 5 kohta)
        icon.getStyle().set("font-size", "80px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");

        //Käytämme gettranslation otsikko ja kuvaus
        H1 title = new H1(getTranslation("home.title"));
        title.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.Margin.Top.MEDIUM);

        Paragraph desc = new Paragraph(getTranslation("home.welcome"));
        desc.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER);

        card.add(icon, title, desc);

        // Saamme oikeat numerot tietokannasta
        String employeesCount = String.valueOf(employeeRepo.count());
        String departmentsCount = String.valueOf(deptRepo.count());
        String projectsCount = String.valueOf(projectRepo.count());

        // Tilastot-osio käännettävillä merkinnöillä
        HorizontalLayout stats = new HorizontalLayout(
                createStat(getTranslation("home.stats.employees"), employeesCount, VaadinIcon.USER),
                createStat(getTranslation("home.stats.departments"), departmentsCount, VaadinIcon.BUILDING),
                createStat(getTranslation("home.stats.projects"), projectsCount, VaadinIcon.BRIEFCASE)
        );
        stats.addClassName(LumoUtility.Margin.Top.XLARGE);
        stats.setSpacing(true);

        add(card, stats);
    }

    private VerticalLayout createStat(String label, String value, VaadinIcon icon) {
        VerticalLayout v = new VerticalLayout();
        v.addClassName("stat-item"); // CSS-tehosteita varten (hover/transition)
        v.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.AlignItems.CENTER
        );
        v.setSpacing(false);
        v.setWidth("160px");

        var i = icon.create();
        i.getStyle().set("color", "var(--lumo-primary-color)");

        Span val = new Span(value);
        val.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.XLARGE);

        Span txt = new Span(label);
        txt.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

        v.add(i, val, txt);
        return v;
    }
}