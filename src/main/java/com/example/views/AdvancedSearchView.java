package com.example.views;

import com.example.data.Employee;
import com.example.data.EmployeeService;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll; // ИМПОРТ ДЛЯ ДОСТУПА ВСЕМ ЗАЛОГИНЕННЫМ

@Route(value = "advanced-search", layout = MainLayout.class)
@PageTitle("Advanced Search | Criteria API")
@PermitAll // JOKAINEN, JOKA ON SYÖTETTY SALASANA ON SALLITTUA.(Admin, User, Super)
public class AdvancedSearchView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);

    private final TextField searchField = new TextField("Name, Surname or Email");
    private final TextField deptField = new TextField("Department (e.g. 'IT')");
    private final DatePicker startDate = new DatePicker("Card Issued From");
    private final DatePicker endDate = new DatePicker("To");

    public AdvancedSearchView(EmployeeService employeeService) {
        this.employeeService = employeeService;

        addClassName("advanced-search-view");
        setSizeFull();

        H2 header = new H2("Criteria Search System");
        header.getStyle().set("color", "var(--lumo-primary-color)");
        header.getStyle().set("border-left", "5px solid var(--lumo-primary-color)");
        header.getStyle().set("padding-left", "15px");
        header.getStyle().set("margin-bottom", "20px");

        configureGrid();

        Button searchBtn = new Button("Search", VaadinIcon.SEARCH.create());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        searchBtn.addClickListener(e -> updateList());

        Button resetBtn = new Button("Reset", e -> {
            searchField.clear();
            deptField.clear();
            startDate.clear();
            endDate.clear();
            updateList();
        });
        resetBtn.addClassName("custom-reset-button");

        HorizontalLayout filters = new HorizontalLayout(searchField, deptField, startDate, endDate, searchBtn, resetBtn);
        filters.setAlignItems(Alignment.BASELINE);

        filters.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Gap.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        add(header, filters, grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addClassName("custom-bordered-grid");

        grid.addColumn(Employee::getFirstName).setHeader("First Name").setSortable(true);
        grid.addColumn(Employee::getLastName).setHeader("Last Name").setSortable(true);
        grid.addColumn(Employee::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(emp -> emp.getDepartment() != null ? emp.getDepartment().getName() : "-").setHeader("Department");
        grid.addColumn(emp -> emp.getAccessCard() != null ? emp.getAccessCard().getIssuedDate().toString() : "-").setHeader("Card Issued");
    }

    private void updateList() {
        grid.setItems(employeeService.findByAdvancedFilter(
                searchField.getValue(), deptField.getValue(), startDate.getValue(), endDate.getValue()
        ));
    }
}