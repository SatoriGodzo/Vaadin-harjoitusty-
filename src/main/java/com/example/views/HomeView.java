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

    // Внедряем репозитории через конструктор для получения реальных данных
    public HomeView(EmployeeRepository employeeRepo,
                    DepartmentRepository deptRepo,
                    ProjectRepository projectRepo) {

        addClassName("home-view"); // Для CSS (Пункт 3 ТЗ)

        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMinHeight("80vh");

        // Главная карточка приветствия
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
        icon.addClassName("welcome-icon"); // Для анимации в CSS (Пункт 5 ТЗ)
        icon.getStyle().set("font-size", "80px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");

        H1 title = new H1("Company Manager Pro");
        title.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.Margin.Top.MEDIUM);

        Paragraph desc = new Paragraph("Welcome! The management system is fully operational. Real-time statistics are shown below.");
        desc.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER);

        card.add(icon, title, desc);

        // Получаем РЕАЛЬНЫЕ цифры из базы данных
        String employeesCount = String.valueOf(employeeRepo.count());
        String departmentsCount = String.valueOf(deptRepo.count());
        String projectsCount = String.valueOf(projectRepo.count());

        // Секция статистики
        HorizontalLayout stats = new HorizontalLayout(
                createStat("Employees", employeesCount, VaadinIcon.USER),
                createStat("Departments", departmentsCount, VaadinIcon.BUILDING),
                createStat("Projects", projectsCount, VaadinIcon.BRIEFCASE)
        );
        stats.addClassName(LumoUtility.Margin.Top.XLARGE);
        stats.setSpacing(true);

        add(card, stats);
    }

    private VerticalLayout createStat(String label, String value, VaadinIcon icon) {
        VerticalLayout v = new VerticalLayout();
        v.addClassName("stat-item"); // Для CSS эффектов (hover/transition)
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