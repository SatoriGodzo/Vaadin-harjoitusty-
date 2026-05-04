package com.example.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class AccessCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Card number is mandatory")
    private String cardNumber;

    @NotNull
    private LocalDate issuedDate;

    // Используем Integer вместо int, чтобы избежать ошибок при пустом поле
    @Min(value = 1, message = "Access level must be at least 1")
    private Integer accessLevel;

    @NotBlank(message = "Fabricator is mandatory")
    private String fabricator;

    // УДАЛЕНО: @AssertTrue (она блокировала сохранение, если isActive = false)
    private boolean isActive;

    @OneToOne(mappedBy = "accessCard")
    private Employee employee;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public Integer getAccessLevel() { return accessLevel; }
    public void setAccessLevel(Integer accessLevel) { this.accessLevel = accessLevel; }
    public String getFabricator() { return fabricator; }
    public void setFabricator(String fabricator) { this.fabricator = fabricator; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}