package com.example.views;

import com.example.data.Project;
import com.example.data.ProjectRepository;
import com.example.data.EmployeeRepository;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.RolesAllowed; // ИМПОРТ

@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects | Company Manager")
@RolesAllowed({"USER","SUPER","ADMIN"})
public class ProjectView extends VerticalLayout {
    private final ProjectRepository repo;
    private Grid<Project> grid = new Grid<>(Project.class, false);
    private ProjectForm form;

    public ProjectView(ProjectRepository repo, EmployeeRepository empRepo) {
        this.repo = repo;
        this.form = new ProjectForm(empRepo.findAll());

        setSizeFull();
        configureGrid();

        form.addListener(ProjectForm.SaveEvent.class, e -> {
            repo.save(e.getProject());
            updateList();
            closeEditor();
        });
        form.addListener(ProjectForm.CloseEvent.class, e -> closeEditor());

        Button addBtn = new Button("Add Project");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> addProject());

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(3, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        add(addBtn, content);
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addClassName("custom-bordered-grid");

        grid.addColumn(Project::getProjectName).setHeader("Project Name").setSortable(true).setAutoWidth(true);
        grid.addColumn(Project::getDeadline).setHeader("Deadline").setSortable(true).setAutoWidth(true);
        grid.addColumn(Project::getClientName).setHeader("Client Name").setSortable(true).setAutoWidth(true);
        grid.addColumn(Project::getBudget).setHeader("Budget").setSortable(true).setAutoWidth(true);
        grid.addColumn(Project::getDescription).setHeader("Description").setFlexGrow(1).setResizable(true);

        grid.asSingleSelect().addValueChangeListener(e -> editProject(e.getValue()));
    }

    private void addProject() {
        grid.asSingleSelect().clear();
        editProject(new Project());
    }

    private void editProject(Project p) {
        if (p == null) {
            closeEditor();
        } else {
            form.setProject(p);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(repo.findAll());
    }
}