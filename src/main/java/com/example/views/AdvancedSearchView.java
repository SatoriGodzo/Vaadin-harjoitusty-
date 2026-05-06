package com.example.views;

import com.example.data.Employee;
import com.example.data.EmployeeService;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;

@Route(value = "advanced-search", layout = MainLayout.class)
@PageTitle("Advanced Search | Criteria API")
public class AdvancedSearchView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);

    private final TextField searchField = new TextField("Name, Surname or Email");
    private final TextField deptField = new TextField("Department (e.g. 'IT')");
    private final DatePicker startDate = new DatePicker("Card Issued From");
    private final DatePicker endDate = new DatePicker("To");

    public AdvancedSearchView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        setSizeFull();

        configureGrid();

        Button searchBtn = new Button("Search", VaadinIcon.SEARCH.create());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.addClickListener(e -> updateList());

        Button resetBtn = new Button("Reset", e -> {
            searchField.clear();
            deptField.clear();
            startDate.clear();
            endDate.clear();
            updateList();
        });

        HorizontalLayout filters = new HorizontalLayout(searchField, deptField, startDate, endDate, searchBtn, resetBtn);
        filters.setVerticalComponentAlignment(Alignment.BASELINE, searchBtn, resetBtn);

        add(filters, grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Employee::getFirstName).setHeader("First Name").setSortable(true);
        grid.addColumn(Employee::getLastName).setHeader("Last Name").setSortable(true);
        grid.addColumn(Employee::getEmail).setHeader("Email").setSortable(true);

        grid.addColumn(emp -> emp.getDepartment() != null ? emp.getDepartment().getName() : "-")
                .setHeader("Department");

        grid.addColumn(emp -> emp.getAccessCard() != null ? emp.getAccessCard().getIssuedDate().toString() : "-")
                .setHeader("Card Issued");
    }

    private void updateList() {
        grid.setItems(employeeService.findByAdvancedFilter(
                searchField.getValue(),
                deptField.getValue(),
                startDate.getValue(),
                endDate.getValue()
        ));
    }
}