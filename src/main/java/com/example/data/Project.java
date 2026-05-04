package com.example.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set; // Заменили List на Set
import java.util.HashSet; // Для инициализации

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is mandatory")
    private String projectName;

    @FutureOrPresent
    private java.time.LocalDate deadline;

    @NotEmpty
    private String clientName;

    @Positive
    private double budget;

    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    // СВЯЗЬ M:N
    // ВАЖНО: Если в Project стоит mappedBy, значит владельцем связи является Employee.
    // Убедись, что в Employee.java есть поле @ManyToMany List<Project> projects;
    @ManyToMany(mappedBy = "projects", fetch = FetchType.EAGER)
    private Set<Employee> employees = new HashSet<>();

    // Геттеры и сеттеры (не забудь поменять на Set)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // Добавь сеттер для ID, если его не было

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

    public Set<Employee> getEmployees() { return employees; }
    public void setEmployees(Set<Employee> employees) { this.employees = employees; }
}