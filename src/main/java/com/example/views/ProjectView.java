package com.example.views;

import com.example.data.Project;
import com.example.data.ProjectRepository;
import com.example.data.EmployeeRepository; // Нужен для списка сотрудников
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "projects", layout = MainLayout.class)
public class ProjectView extends VerticalLayout {
    private final ProjectRepository repo;
    private Grid<Project> grid = new Grid<>(Project.class);
    private ProjectForm form;

    public ProjectView(ProjectRepository repo, EmployeeRepository empRepo) {
        this.repo = repo;
        // Передаем всех сотрудников в форму для связи M:N
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
        addBtn.addClickListener(e -> addProject());

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        add(addBtn, content);
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setColumns("projectName", "deadline", "clientName", "budget", "description");
        grid.asSingleSelect().addValueChangeListener(e -> editProject(e.getValue()));
    }

    private void addProject() {
        grid.asSingleSelect().clear();
        editProject(new Project()); // Вот здесь создается НОВЫЙ проект
    }

    private void editProject(Project p) {
        if (p == null) {
            closeEditor();
        } else {
            form.setProject(p);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setVisible(false);
    }

    private void updateList() { grid.setItems(repo.findAll()); }
}