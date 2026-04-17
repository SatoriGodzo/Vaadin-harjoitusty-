package com.example.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is mandatory") // 1
    private String projectName;

    @FutureOrPresent // 2
    private java.time.LocalDate deadline;

    @NotEmpty // 3
    private String clientName;

    @Positive // 4
    private double budget;

    @Size(min = 10) // 5
    private String description;

    // СВЯЗЬ M:N
    @ManyToMany(mappedBy = "projects")
    private List<Employee> employees;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public java.time.LocalDate getDeadline() { return deadline; }
    public void setDeadline(java.time.LocalDate deadline) { this.deadline = deadline; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}