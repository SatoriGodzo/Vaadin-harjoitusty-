package com.example.views;

import com.example.data.Department;
import com.example.data.DepartmentRepository;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll; // ИМПОРТ

@Route(value = "departments", layout = MainLayout.class)
@PageTitle("Departments")
@PermitAll // ДОСТУП ДЛЯ ВСЕХ (Admin, User, Super)
public class DepartmentView extends VerticalLayout {
    private final DepartmentRepository repo;
    private Grid<Department> grid = new Grid<>(Department.class, false);
    private DepartmentForm form;

    public DepartmentView(DepartmentRepository repo) {
        this.repo = repo;
        this.form = new DepartmentForm();

        setSizeFull();
        configureGrid();

        form.addListener(DepartmentForm.SaveEvent.class, this::saveDepartment);
        form.addListener(DepartmentForm.DeleteEvent.class, this::deleteDepartment);
        form.addListener(DepartmentForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        Button addBtn = new Button("Add Department");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> addDepartment());

        add(addBtn, content);
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Department::getName).setHeader("Name");
        grid.addColumn(Department::getOffice).setHeader("Office");
        grid.addColumn(Department::getContactEmail).setHeader("Email");
        grid.addColumn(Department::getFloor).setHeader("Floor");
        grid.addColumn(Department::getBudgetCode).setHeader("Budget Code");

        grid.asSingleSelect().addValueChangeListener(e -> editDepartment(e.getValue()));
    }

    private void saveDepartment(DepartmentForm.SaveEvent e) {
        if (e.getDepartment() != null) {
            repo.save(e.getDepartment());
            updateList();
            closeEditor();
        }
    }

    private void deleteDepartment(DepartmentForm.DeleteEvent e) {
        if (e.getDepartment() != null) {
            repo.delete(e.getDepartment());
            updateList();
            closeEditor();
        }
    }

    private void updateList() {
        grid.setItems(repo.findAll());
    }

    private void editDepartment(Department d) {
        if (d == null) {
            closeEditor();
        } else {
            form.setDepartment(d);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setDepartment(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addDepartment() {
        grid.asSingleSelect().clear();
        editDepartment(new Department());
    }
}