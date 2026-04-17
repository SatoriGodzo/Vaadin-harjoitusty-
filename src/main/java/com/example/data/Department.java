package com.example.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department name is required")
    private String name;

    @Size(min = 2, message = "Office location too short")
    private String office;

    @Email
    private String contactEmail;

    @Min(1)
    private int floor;

    @NotNull
    private String budgetCode;

    // 1:N Relaatio - Yhdellä osastolla on monta työntekijää
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    // Getterit ja Setterit
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOffice() { return office; }
    public void setOffice(String office) { this.office = office; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public String getBudgetCode() { return budgetCode; }
    public void setBudgetCode(String budgetCode) { this.budgetCode = budgetCode; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}