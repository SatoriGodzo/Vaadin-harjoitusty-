package com.example.views;

import com.example.data.*;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "employees", layout = MainLayout.class)
@RolesAllowed({"ROLE_ADMIN","SUPER"})
public class EmployeeView extends VerticalLayout implements HasDynamicTitle {

    private final EmployeeRepository repository;
    private final DepartmentRepository departmentRepository;
    private final AccessCardRepository cardRepository;
    private final ProjectRepository projectRepository;

    private Grid<Employee> grid = new Grid<>(Employee.class, false);
    private EmployeeForm form;

    @Override
    public String getPageTitle() {
        return getTranslation("menu.employees");
    }

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

        // --- СОЗДАНИЕ ТУЛБАРА ---

        Button addEmployeeButton = new Button(getTranslation("emp.button.add"), click -> addEmployee());
        addEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".csv");
        upload.setUploadButton(new Button("Import CSV", new Icon(VaadinIcon.UPLOAD)));
        upload.setDropAllowed(false);
        upload.addSucceededListener(event -> importCsv(buffer.getInputStream()));

        Anchor exportLink = createExportAnchor();

        HorizontalLayout toolbar = new HorizontalLayout(addEmployeeButton, upload, exportLink);
        toolbar.setVerticalComponentAlignment(Alignment.END, upload);

        add(toolbar, content);
        updateList();
        closeEditor();
    }

    private void importCsv(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            for (String line : lines) {
                String[] parts = line.split(";");
                if (parts.length < 3) continue;

                String fName = parts[0].trim();
                if (fName.equalsIgnoreCase("First Name") || fName.equalsIgnoreCase("Etunimi") || fName.isEmpty()) continue;

                Employee emp = new Employee();
                emp.setFirstName(fName);
                emp.setLastName(parts[1].trim());
                emp.setEmail(parts[2].trim());

                if (parts.length > 3) {
                    try {
                        emp.setAge(Integer.parseInt(parts[3].trim()));
                    } catch (Exception e) {
                        emp.setAge(30);
                    }
                } else {
                    emp.setAge(30);
                }

                if (parts.length > 4) {
                    emp.setAddress(parts[4].trim());
                }

                repository.save(emp);
            }
            updateList();
            Notification.show("Import successful!");
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private Anchor createExportAnchor() {
        StreamResource source = new StreamResource("employees_full.csv", () -> {
            StringBuilder csv = new StringBuilder("First Name;Last Name;Email;Age;Address;Access Card;Department;Projects\n");

            repository.findAll().forEach(emp -> {
                csv.append(emp.getFirstName()).append(";")
                        .append(emp.getLastName()).append(";")
                        .append(emp.getEmail()).append(";")
                        .append(emp.getAge()).append(";")
                        .append(emp.getAddress() != null ? emp.getAddress() : "-").append(";")
                        .append(emp.getAccessCard() != null ? emp.getAccessCard().getCardNumber() : "-").append(";")
                        .append(emp.getDepartment() != null ? emp.getDepartment().getName() : "-").append(";")
                        .append(emp.getProjects() == null ? "" :
                                emp.getProjects().stream()
                                        .map(Project::getProjectName)
                                        .collect(Collectors.joining(", ")))
                        .append("\n");
            });
            return new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
        });

        Anchor anchor = new Anchor(source, "");
        Button button = new Button("Export CSV", new Icon(VaadinIcon.DOWNLOAD));
        anchor.getElement().setAttribute("download", true);
        anchor.add(button);
        return anchor;
    }

    @Transactional
    private void saveEmployee(Employee employee) {
        AccessCard selectedTrackedCard = employee.getAccessCard();
        employee.setAccessCard(null);

        Employee savedEmployee = repository.saveAndFlush(employee);

        if (selectedTrackedCard != null) {
            AccessCard cardFromDb = cardRepository.findById(selectedTrackedCard.getId()).orElse(null);

            if (cardFromDb != null) {
                if (cardFromDb.getEmployee() != null && !cardFromDb.getEmployee().equals(savedEmployee)) {
                    cardFromDb.getEmployee().setAccessCard(null);
                }

                savedEmployee.setAccessCard(cardFromDb);
                cardFromDb.setEmployee(savedEmployee);

                cardRepository.saveAndFlush(cardFromDb);
                repository.saveAndFlush(savedEmployee);
            }
        }

        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Employee::getFirstName).setHeader(getTranslation("emp.grid.first-name"));
        grid.addColumn(Employee::getLastName).setHeader(getTranslation("emp.grid.last-name"));
        grid.addColumn(Employee::getEmail).setHeader(getTranslation("emp.grid.email"));
        grid.addColumn(Employee::getAge).setHeader(getTranslation("emp.grid.age"));
        grid.addColumn(Employee::getAddress).setHeader(getTranslation("emp.grid.address"));

        grid.addColumn(emp -> emp.getAccessCard() != null ?
                emp.getAccessCard().getCardNumber() : "-").setHeader(getTranslation("emp.grid.card"));

        grid.addColumn(emp -> emp.getDepartment() != null ?
                emp.getDepartment().getName() : "-").setHeader(getTranslation("emp.grid.dept"));

        grid.addColumn(emp -> emp.getProjects() == null ? "" :
                        emp.getProjects().stream()
                                .map(Project::getProjectName)
                                .collect(Collectors.joining(", ")))
                .setHeader(getTranslation("emp.grid.projects"));

        // --- НОВЫЕ КОЛОНКИ АУДИТА ---
        grid.addColumn(emp -> emp.getLastModifiedBy() != null ? emp.getLastModifiedBy() : "-")
                .setHeader("Modified By");

        grid.addColumn(emp -> emp.getLastModifiedDate() != null ?
                        emp.getLastModifiedDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "-")
                .setHeader("Modified At");

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