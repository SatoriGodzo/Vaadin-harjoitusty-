package com.example.views;

import com.example.data.AccessCard;
import com.example.data.Employee;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class AccessCardForm extends FormLayout {
    TextField cardNumber = new TextField("Card Number");
    DatePicker issuedDate = new DatePicker("Issued Date");
    IntegerField accessLevel = new IntegerField("Access Level");
    TextField fabricator = new TextField("Fabricator");
    TextField description = new TextField("Description");
    Checkbox isActive = new Checkbox("Is Active");
    ComboBox<Employee> employee = new ComboBox<>("Owner (Employee)");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<AccessCard> binder = new BeanValidationBinder<>(AccessCard.class);

    public AccessCardForm() {
        addClassName("access-card-form");

        employee.setItemLabelGenerator(emp -> emp.getFirstName() + " " + emp.getLastName());

        // Isactiven nimenomainen sitovuus
        binder.forField(isActive)
                .bind(AccessCard::isActive, AccessCard::setActive);

        // Muiden kenttien automaattinen Sidonta
        binder.bindInstanceFields(this);

        add(
                cardNumber,
                issuedDate,
                accessLevel,
                fabricator,
                description,
                isActive,
                employee,
                createButtonsLayout()
        );
    }

    public void setEmployeeItems(List<Employee> employees) {
        employee.setItems(employees);
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

    /**
     * KIINTEÄ MENETELMÄ:
     * Tarjoaa CRUD-toiminnan 1: 1-viestintään kortin puolelta.
     */
    private void validateAndSave() {
        try {
            AccessCard card = binder.getBean();
            // Tietojen kopiointi käyttöliittymästä objektiin
            binder.writeBean(card);

            // Synkronointi: Jos työntekijä valitaan lomakkeeseen, linkitämme kortin häneen.
            // Ilman tätä tietokannan yhteyttä ei päivitetä, koska kulkukortti on mapped by side.
            if (card.getEmployee() != null) {
                card.getEmployee().setAccessCard(card);
            }

            fireEvent(new SaveEvent(this, card));
        } catch (ValidationException e) {
            // Validointivirheet värittävät käyttöliittymän kentät
        }
    }

    public void setCard(AccessCard card) {
        binder.setBean(card);
    }

    // --- Tapahtumat (Events) ---
    public static abstract class AccessCardFormEvent extends ComponentEvent<AccessCardForm> {
        private AccessCard card;

        protected AccessCardFormEvent(AccessCardForm source, AccessCard card) {
            super(source, false);
            this.card = card;
        }

        public AccessCard getCard() {
            return card;
        }
    }

    public static class SaveEvent extends AccessCardFormEvent {
        SaveEvent(AccessCardForm source, AccessCard card) {
            super(source, card);
        }
    }

    public static class DeleteEvent extends AccessCardFormEvent {
        DeleteEvent(AccessCardForm source, AccessCard card) {
            super(source, card);
        }
    }

    public static class CloseEvent extends AccessCardFormEvent {
        CloseEvent(AccessCardForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}