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
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EmployeeForm extends FormLayout {
    // Kenttien luominen. Poistamme tekstin sulkeissa, koska asetamme sen gettranslationin kautta konstruktorissa.
    TextField firstName = new TextField();
    TextField lastName = new TextField();
    EmailField email = new EmailField();
    IntegerField age = new IntegerField();
    TextField address = new TextField();

    ComboBox<Department> department = new ComboBox<>();
    ComboBox<AccessCard> accessCard = new ComboBox<>();
    MultiSelectComboBox<Project> projects = new MultiSelectComboBox<>();

    Button save = new Button();
    Button delete = new Button();
    Button close = new Button();

    Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);

    public EmployeeForm(List<Department> departments, List<AccessCard> cards, List<Project> allProjects) {
        addClassName("employee-form");

        this.getStyle().set("padding", "var(--lumo-space-l)");
        this.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        this.getStyle().set("background-color", "var(--lumo-base-color)");
        this.getStyle().set("border-radius", "var(--lumo-border-radius-m)");

        // УСТАНОВКА ПЕРЕВОДОВ ДЛЯ ПОЛЕЙ (Берем те же ключи, что и для таблицы)
        firstName.setLabel(getTranslation("emp.grid.first-name"));
        lastName.setLabel(getTranslation("emp.grid.last-name"));
        email.setLabel(getTranslation("emp.grid.email"));
        age.setLabel(getTranslation("emp.grid.age"));
        address.setLabel(getTranslation("emp.grid.address"));
        department.setLabel(getTranslation("emp.grid.dept"));
        accessCard.setLabel(getTranslation("emp.grid.card"));
        projects.setLabel(getTranslation("emp.grid.projects"));

        // УСТАНОВКА ПЕРЕВОДОВ ДЛЯ КНОПОК
        save.setText(getTranslation("form.save"));
        delete.setText(getTranslation("form.delete"));
        close.setText(getTranslation("form.cancel"));

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

    public void setAvailableCards(List<AccessCard> cards) {
        accessCard.setItems(cards);
    }

    /**
     * Lisätty käsittely ValidationException ja pakotettu synkronointi 1:1
     */
    private void validateAndSave() {
        try {
            Employee employee = binder.getBean();
            // 1. ensin kirjoitamme tiedot kaikista kentistä (mukaan lukien kartan Kombobox) kohteeseen
            binder.writeBean(employee);

            // 2. Luomme kaksisuuntaista viestintää
            if (employee.getAccessCard() != null) {
                // Kerromme kartalle, kuka sen uusi omistaja on
                employee.getAccessCard().setEmployee(employee);
            }

            // 3.Näkymän ilmoittaminen siitä, että objekti on valmis tallennettavaksi tietokantaan.
            fireEvent(new SaveEvent(this, employee));

        } catch (ValidationException e) {
            // Validointivirheet (esimerkiksi ikä < 18) näkyvät käyttöliittymässä automaattisesti.
        }
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClassName("form-save-button");

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setEmployee(Employee employee) {
        binder.setBean(employee);
    }


    public static abstract class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {
        private Employee employee;
        protected EmployeeFormEvent(EmployeeForm source, Employee employee) {
            super(source, false);
            this.employee = employee;
        }
        public Employee getEmployee() { return employee; }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, Employee employee) { super(source, employee); }
    }
    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, Employee employee) { super(source, employee); }
    }
    public static class CloseEvent extends EmployeeFormEvent {
        CloseEvent(EmployeeForm source) { super(source, null); }
    }

    public <T extends ComponentEvent<?>> Registration addEmployeeFormListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}