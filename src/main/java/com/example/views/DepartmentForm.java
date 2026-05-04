package com.example.views;

import com.example.data.Department;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class DepartmentForm extends FormLayout {
    TextField name = new TextField("Department Name");
    TextField office = new TextField("Office Location");
    EmailField contactEmail = new EmailField("Contact Email");
    IntegerField floor = new IntegerField("Floor");
    TextField budgetCode = new TextField("Budget Code");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Department> binder = new BeanValidationBinder<>(Department.class);

    public DepartmentForm() {
        addClassName("department-form");
        binder.bindInstanceFields(this);
        add(name, office, contactEmail, floor, budgetCode, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        Department dept = binder.getBean(); // Получаем текущий объект (старый или новый)
        if (dept == null) return;

        try {
            // Пытаемся записать данные из полей ввода в объект
            binder.writeBean(dept);
            // Если ошибок нет, вызываем событие сохранения
            fireEvent(new SaveEvent(this, dept));
        } catch (ValidationException e) {
            // Если данные невалидны, поля подцветятся красным, и мы ничего не сохраним
        }
    }

    public void setDepartment(Department dept) {
        binder.setBean(dept); // Важно: привязываем конкретный объект к биндеру
    }

    // События
    public static abstract class DepartmentFormEvent extends ComponentEvent<DepartmentForm> {
        private Department dept;
        protected DepartmentFormEvent(DepartmentForm source, Department dept) {
            super(source, false);
            this.dept = dept;
        }
        public Department getDepartment() { return dept; }
    }

    public static class SaveEvent extends DepartmentFormEvent {
        SaveEvent(DepartmentForm source, Department dept) { super(source, dept); }
    }

    public static class DeleteEvent extends DepartmentFormEvent {
        DeleteEvent(DepartmentForm source, Department dept) { super(source, dept); }
    }

    public static class CloseEvent extends DepartmentFormEvent {
        CloseEvent(DepartmentForm source) { super(source, null); }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}