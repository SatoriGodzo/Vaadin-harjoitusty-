package com.example.views;

import com.example.data.*;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "employees", layout = MainLayout.class)
@PageTitle("Employees | CRUD")
public class EmployeeView extends VerticalLayout {

    private final EmployeeRepository repository;
    private final DepartmentRepository departmentRepository;
    private final AccessCardRepository cardRepository;
    private final ProjectRepository projectRepository;

    private Grid<Employee> grid = new Grid<>(Employee.class, false);
    private EmployeeForm form;

    public EmployeeView(EmployeeRepository repository,
                        DepartmentRepository departmentRepository,
                        AccessCardRepository cardRepository,
                        ProjectRepository projectRepository) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.cardRepository = cardRepository;
        this.projectRepository = projectRepository;

        setSizeFull();
        configureGrid();

        // Создаем форму один раз при инициализации
        form = new EmployeeForm(
                departmentRepository.findAll(),
                new ArrayList<>(), // Списки обновим ниже
                projectRepository.findAll()
        );
        form.setWidth("25em");

        form.addEmployeeFormListener(EmployeeForm.SaveEvent.class, e -> {
            repository.save(e.getEmployee());
            updateList();
            closeEditor();
        });

        form.addEmployeeFormListener(EmployeeForm.DeleteEvent.class, e -> {
            repository.delete(e.getEmployee());
            updateList();
            closeEditor();
        });

        form.addEmployeeFormListener(EmployeeForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        Button addEmployeeButton = new Button("Add Employee", click -> addEmployee());
        addEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(addEmployeeButton, content);
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Employee::getFirstName).setHeader("First Name");
        grid.addColumn(Employee::getLastName).setHeader("Last Name");
        grid.addColumn(Employee::getEmail).setHeader("Email");
        grid.addColumn(Employee::getAge).setHeader("Age");
        grid.addColumn(Employee::getAddress).setHeader("Address");
        grid.addColumn(emp -> emp.getAccessCard() != null ? emp.getAccessCard().getCardNumber() : "-").setHeader("Card (1:1)");
        grid.addColumn(emp -> emp.getDepartment() != null ? emp.getDepartment().getName() : "-").setHeader("Dept (1:N)");
        grid.addColumn(emp -> emp.getProjects() == null ? "" :
                        emp.getProjects().stream().map(Project::getProjectName).collect(Collectors.joining(", ")))
                .setHeader("Projects (M:N)");

        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private void updateList() {
        grid.setItems(repository.findAll());
    }

    private void editEmployee(Employee employee) {
        if (employee == null) {
            closeEditor();
        } else {
            // ПЕРЕД открытием формы обновляем список доступных карт
            updateAvailableCards(employee);
            form.setEmployee(employee);
            form.setVisible(true);
        }
    }

    private void updateAvailableCards(Employee currentEmployee) {
        // 1. Берем все карты из базы
        List<AccessCard> allCards = cardRepository.findAll();

        // 2. Оставляем только те, что не привязаны НИ К КОМУ,
        //    ЛИБО привязаны именно к текущему редактируемому сотруднику
        List<AccessCard> availableCards = allCards.stream()
                .filter(card -> card.getEmployee() == null || card.getEmployee().equals(currentEmployee))
                .collect(Collectors.toList());

        form.setAvailableCards(availableCards);
    }

    private void closeEditor() {
        form.setEmployee(null);
        form.setVisible(false);
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        Employee newEmployee = new Employee();
        newEmployee.setProjects(new ArrayList<>());
        editEmployee(newEmployee);
    }
}