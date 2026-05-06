package com.example.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class AccessCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Card number is mandatory") // 1
    private String cardNumber;

    @NotNull(message = "Issued date is required") // 2
    private LocalDate issuedDate;

    @Min(value = 1, message = "Access level must be at least 1") // 3
    private Integer accessLevel;

    @NotBlank(message = "Fabricator is mandatory") // 4
    private String fabricator;

    @Size(min = 5, message = "Description must be at least 5 characters") // 5 (Пятая!)
    private String description;

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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}