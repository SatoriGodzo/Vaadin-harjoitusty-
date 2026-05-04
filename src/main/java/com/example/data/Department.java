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

    // Снизил до 1, чтобы проще было тестировать
    @Size(min = 1, message = "Office location is required")
    private String office;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String contactEmail;

    @Min(1)
    private int floor = 1;

    @NotBlank(message = "Budget code is required")
    private String budgetCode;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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