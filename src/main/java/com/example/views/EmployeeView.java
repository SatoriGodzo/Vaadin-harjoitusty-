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
import jakarta.annotation.security.RolesAllowed;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "employees", layout = MainLayout.class)
@RolesAllowed("ROLE_ADMIN")
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

        form = new EmployeeForm(
                departmentRepository.findAll(),
                new ArrayList<>(),
                projectRepository.findAll()
        );
        form.setWidth("25em");

        // Используем исправленный метод для сохранения
        form.addEmployeeFormListener(EmployeeForm.SaveEvent.class, e -> saveEmployee(e.getEmployee()));

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

    /**
     * Исправленная логика сохранения для One-to-One связи.
     * Принудительно прошивает существующую карту в нового сотрудника.
     */
    @Transactional
    private void saveEmployee(Employee employee) {
        // 1. Сохраняем "голого" сотрудника, чтобы база выдала ему ID
        AccessCard selectedTrackedCard = employee.getAccessCard();
        employee.setAccessCard(null);

        // saveAndFlush принудительно отправляет INSERT в базу прямо сейчас
        Employee savedEmployee = repository.saveAndFlush(employee);

        // 2. Если в форме была выбрана существующая карта
        if (selectedTrackedCard != null) {
            // Достаем ее из БД заново в текущую транзакцию
            AccessCard cardFromDb = cardRepository.findById(selectedTrackedCard.getId()).orElse(null);

            if (cardFromDb != null) {
                // Если карта раньше была у другого — отвязываем (защита 1:1)
                if (cardFromDb.getEmployee() != null && !cardFromDb.getEmployee().equals(savedEmployee)) {
                    cardFromDb.getEmployee().setAccessCard(null);
                }

                // Шьем связь жестко с двух сторон
                savedEmployee.setAccessCard(cardFromDb);
                cardFromDb.setEmployee(savedEmployee);

                // Сначала обновляем карту, потом обновляем сотрудника
                cardRepository.saveAndFlush(cardFromDb);
                repository.saveAndFlush(savedEmployee);
            }
        }

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

        // Отображение номера карты
        grid.addColumn(emp -> emp.getAccessCard() != null ?
                emp.getAccessCard().getCardNumber() : "-").setHeader("Card (1:1)");

        grid.addColumn(emp -> emp.getDepartment() != null ?
                emp.getDepartment().getName() : "-").setHeader("Dept (1:N)");

        grid.addColumn(emp -> emp.getProjects() == null ? "" :
                        emp.getProjects().stream()
                                .map(Project::getProjectName)
                                .collect(Collectors.joining(", ")))
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
            updateAvailableCards(employee);
            form.setEmployee(employee);
            form.setVisible(true);
        }
    }

    private void updateAvailableCards(Employee currentEmployee) {
        List<AccessCard> allCards = cardRepository.findAll();

        // Показываем только свободные карты или ту, что уже у этого сотрудника
        List<AccessCard> availableCards = allCards.stream()
                .filter(card -> card.getEmployee() == null ||
                        (currentEmployee.getId() != null && card.getEmployee().getId().equals(currentEmployee.getId())))
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