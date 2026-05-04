package com.example.views;

import com.example.data.Project;
import com.example.data.Employee; // Добавь этот импорт
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;

public class ProjectForm extends FormLayout {
    TextField projectName = new TextField("Project Name");
    DatePicker deadline = new DatePicker("Deadline");
    TextField clientName = new TextField("Client Name");
    NumberField budget = new NumberField("Budget");
    TextField description = new TextField("Description");

    // СВЯЗЬ M:N: Выбор сотрудников для проекта
    MultiSelectComboBox<Employee> employees = new MultiSelectComboBox<>("Assigned Employees");

    Button save = new Button("Save");
    Button close = new Button("Cancel");

    Binder<Project> binder = new BeanValidationBinder<>(Project.class);
    private Project project;

    public ProjectForm(List<Employee> allEmployees) {
        addClassName("project-form");

        // Настройка выбора сотрудников
        employees.setItems(allEmployees);
        employees.setItemLabelGenerator(Employee::getLastName); // Показываем фамилии

        binder.bindInstanceFields(this);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> validateAndSave());
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        add(projectName, deadline, clientName, budget, description, employees, new HorizontalLayout(save, close));
    }

    public void setProject(Project p) {
        this.project = p;
        binder.readBean(p); // Загружаем данные объекта в поля
    }

    private void validateAndSave() {
        try {
            if (project != null) {
                binder.writeBean(project); // Записываем данные из полей в объект
                fireEvent(new SaveEvent(this, project));
            }
        } catch (ValidationException e) {
            // Ошибки валидации покажутся на полях автоматически
        }
    }

    // События
    public static abstract class ProjectFormEvent extends ComponentEvent<ProjectForm> {
        private Project p;
        protected ProjectFormEvent(ProjectForm s, Project p) { super(s, false); this.p = p; }
        public Project getProject() { return p; }
    }

    public static class SaveEvent extends ProjectFormEvent {
        SaveEvent(ProjectForm s, Project p) { super(s, p); }
    }

    public static class CloseEvent extends ProjectFormEvent {
        CloseEvent(ProjectForm s) { super(s, null); }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}