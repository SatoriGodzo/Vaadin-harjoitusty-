package com.example.views;

import com.example.data.AccessCard;
import com.example.data.AccessCardRepository;
import com.example.data.Employee;
import com.example.data.EmployeeRepository;
import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "cards", layout = MainLayout.class)
@PageTitle("Access Cards")
public class AccessCardView extends VerticalLayout {

    private final AccessCardRepository repository;
    private final EmployeeRepository employeeRepository;
    private Grid<AccessCard> grid = new Grid<>(AccessCard.class, false);
    private AccessCardForm form;

    public AccessCardView(AccessCardRepository repository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        setSizeFull();
        configureGrid();

        form = new AccessCardForm();
        form.setWidth("25em");

        form.addListener(AccessCardForm.SaveEvent.class, this::saveCard);
        form.addListener(AccessCardForm.DeleteEvent.class, this::deleteCard);
        form.addListener(AccessCardForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        Button addCardButton = new Button("Add Card");
        addCardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCardButton.addClickListener(click -> addCard());

        add(addCardButton, content);
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(AccessCard::getCardNumber).setHeader("Card Number");
        grid.addColumn(AccessCard::getIssuedDate).setHeader("Issued Date");
        grid.addColumn(AccessCard::getAccessLevel).setHeader("Access Level");
        grid.addColumn(AccessCard::getFabricator).setHeader("Fabricator");
        grid.addColumn(AccessCard::getDescription).setHeader("Description"); // Новая колонка
        grid.addColumn(card -> card.isActive() ? "Yes" : "No").setHeader("Active");
        grid.addColumn(card -> card.getEmployee() != null ?
                        card.getEmployee().getFirstName() + " " + card.getEmployee().getLastName() : "-")
                .setHeader("Owner");

        grid.asSingleSelect().addValueChangeListener(event -> editCard(event.getValue()));
    }

    private void saveCard(AccessCardForm.SaveEvent event) {
        AccessCard card = event.getCard();
        Employee owner = card.getEmployee();

        // Сначала сохраняем карту, чтобы у нее появился ID
        repository.save(card);

        if (owner != null) {
            // Привязываем карту к сотруднику и сохраняем его
            owner.setAccessCard(card);
            employeeRepository.save(owner);
        }

        updateList();
        closeEditor();
    }

    private void deleteCard(AccessCardForm.DeleteEvent event) {
        AccessCard card = event.getCard();
        Employee owner = card.getEmployee();

        if (owner != null) {
            owner.setAccessCard(null);
            employeeRepository.save(owner);
        }

        repository.delete(card);
        updateList();
        closeEditor();
    }

    private void updateAvailableEmployees(AccessCard currentCard) {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> availableEmployees = allEmployees.stream()
                .filter(emp -> emp.getAccessCard() == null ||
                        (currentCard.getEmployee() != null && emp.getId().equals(currentCard.getEmployee().getId())))
                .collect(Collectors.toList());
        form.setEmployeeItems(availableEmployees);
    }

    private void addCard() {
        grid.asSingleSelect().clear();
        editCard(new AccessCard());
    }

    private void editCard(AccessCard card) {
        if (card == null) {
            closeEditor();
        } else {
            updateAvailableEmployees(card);
            form.setCard(card);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setCard(null);
        form.setVisible(false);
    }

    private void updateList() {
        grid.setItems(repository.findAll());
    }
}