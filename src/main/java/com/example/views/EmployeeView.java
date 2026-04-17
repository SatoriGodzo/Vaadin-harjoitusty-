package com.example.views;

import com.example.data.Employee;
import com.example.data.EmployeeRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle; // НОВЫЙ ИМПОРТ
import com.vaadin.flow.router.Route;
import java.util.stream.Collectors;

// layout = MainLayout.class говорит Ваадину вставить эту таблицу внутрь нашего меню
@Route(value = "employees", layout = MainLayout.class)
@PageTitle("Employees | Vaadin App")
public class EmployeeView extends VerticalLayout {

    public EmployeeView(EmployeeRepository repository) {
        // Настройка таблицы
        Grid<Employee> grid = new Grid<>(Employee.class, false);

        grid.addColumn(Employee::getFirstName).setHeader("First Name");
        grid.addColumn(Employee::getLastName).setHeader("Last Name");
        grid.addColumn(Employee::getEmail).setHeader("Email");
        grid.addColumn(Employee::getAge).setHeader("Age");
        grid.addColumn(Employee::getAddress).setHeader("Address");

        // 1:1 Relation
        grid.addColumn(emp -> emp.getAccessCard() != null ?
                        emp.getAccessCard().getCardNumber() : "No Card")
                .setHeader("Access Card");

        // 1:N Relation
        grid.addColumn(emp -> emp.getDepartment() != null ?
                        emp.getDepartment().getName() : "No Dept")
                .setHeader("Department");

        // M:N Relation
        grid.addColumn(emp -> emp.getProjects() != null ?
                        emp.getProjects().stream()
                                .map(p -> p.getProjectName())
                                .collect(Collectors.joining(", ")) : "No Projects")
                .setHeader("Projects");

        grid.setItems(repository.findAll());

        add(grid);
        setSizeFull();
    }
}