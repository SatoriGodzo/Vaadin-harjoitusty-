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

    @NotNull // 2
    private LocalDate issuedDate;

    @Min(value = 1, message = "Access level must be at least 1") // 3
    private int accessLevel;

    @NotBlank // 4
    private String fabricator;

    @AssertTrue(message = "Card must be active") // 5
    private boolean isActive;

    //  1:1  Employee
    @OneToOne(mappedBy = "accessCard")
    private Employee employee;

    public Long getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
    public String getFabricator() { return fabricator; }
    public void setFabricator(String fabricator) { this.fabricator = fabricator; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}