package com.example.views;

import com.example.data.*;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EmployeeForm extends FormLayout {
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    EmailField email = new EmailField("Email");
    IntegerField age = new IntegerField("Age");
    TextField address = new TextField("Address");

    ComboBox<Department> department = new ComboBox<>("Department");
    ComboBox<AccessCard> accessCard = new ComboBox<>("Access Card");
    MultiSelectComboBox<Project> projects = new MultiSelectComboBox<>("Projects");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);

    public EmployeeForm(List<Department> departments, List<AccessCard> cards, List<Project> allProjects) {
        addClassName("employee-form");

        department.setItems(departments);
        department.setItemLabelGenerator(Department::getName);

        accessCard.setItemLabelGenerator(AccessCard::getCardNumber);
        setAvailableCards(cards);

        projects.setItems(allProjects);
        projects.setItemLabelGenerator(Project::getProjectName);

        binder.forField(projects)
                .bind(
                        emp -> new HashSet<>(emp.getProjects() != null ? emp.getProjects() : new ArrayList<>()),
                        (emp, set) -> emp.setProjects(new ArrayList<>(set))
                );

        binder.bindInstanceFields(this);
        add(firstName, lastName, email, age, address, department, accessCard, projects, createButtonsLayout());
    }

    // Метод для обновления списка карт "на лету"
    public void setAvailableCards(List<AccessCard> cards) {
        accessCard.setItems(cards);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            Employee employee = binder.getBean();
            if (employee.getAccessCard() != null) {
                employee.getAccessCard().setEmployee(employee);
            }
            fireEvent(new SaveEvent(this, employee));
        }
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

    public void setEmployee(Employee employee) { binder.setBean(employee); }

    public static abstract class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {
        private Employee employee;
        protected EmployeeFormEvent(EmployeeForm source, Employee employee) {
            super(source, false);
            this.employee = employee;
        }
        public Employee getEmployee() { return employee; }
    }

    public static class SaveEvent extends EmployeeFormEvent { SaveEvent(EmployeeForm source, Employee employee) { super(source, employee); } }
    public static class DeleteEvent extends EmployeeFormEvent { DeleteEvent(EmployeeForm source, Employee employee) { super(source, employee); } }
    public static class CloseEvent extends EmployeeFormEvent { CloseEvent(EmployeeForm source) { super(source, null); } }

    public <T extends ComponentEvent<?>> Registration addEmployeeFormListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}