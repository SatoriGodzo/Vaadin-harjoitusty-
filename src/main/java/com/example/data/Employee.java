package com.example.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class) // Включаем "прослушку" аудита
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nimi on pakollinen")
    @Size(min = 2, message = "Liian lyhyt nimi")
    private String firstName;

    @NotBlank(message = "Sukunimi on pakollinen")
    private String lastName;

    @Email(message = "Posti virhe")
    @NotBlank
    private String email;

    @Min(value = 18, message = "Ikä virhe")
    private int age;

    @NotBlank(message = "Osoite puuttuu")
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "access_card_id", referencedColumnName = "id")
    private AccessCard accessCard;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_projects",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    private List<Project> projects;

    // --- AUDITOINTIKENTÄT (kevät täyttää ne itsessään) ---

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    // --- VAKIO GETTERIT JA SETTERIT ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public AccessCard getAccessCard() { return accessCard; }

    public void setAccessCard(AccessCard accessCard) {
        this.accessCard = accessCard;
        if (accessCard != null) {
            accessCard.setEmployee(this);
        }
    }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    // --- GETTERS tarkastusta varten (jotta ne voidaan näyttää taulukossa)---

    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
}