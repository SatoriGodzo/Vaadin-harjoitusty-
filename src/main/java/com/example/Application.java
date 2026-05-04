package com.example;

import com.example.data.*;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
public class Application implements AppShellConfigurator {
    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

    @Bean
    public CommandLineRunner initData(EmployeeRepository employeeRepo, DepartmentRepository deptRepo, ProjectRepository projectRepo) {
        return args -> {
            if (employeeRepo.count() == 0) {
                Department dept = new Department();
                dept.setName("IT Support");
                dept.setOffice("HQ-01");
                dept.setContactEmail("it@company.com");
                dept.setFloor(2);
                dept.setBudgetCode("B-100");
                deptRepo.save(dept);

                Project proj = new Project();
                proj.setProjectName("AI Integration");
                proj.setDeadline(LocalDate.now().plusMonths(6));
                proj.setClientName("Google");
                proj.setBudget(50000.0);
                proj.setDescription("Full scale AI integration project.");
                projectRepo.save(proj);

                AccessCard card = new AccessCard();
                card.setCardNumber("GOLD-777");
                card.setIssuedDate(LocalDate.now());
                card.setAccessLevel(5);
                card.setFabricator("Assa Abloy");
                card.setActive(true);

                Employee emp = new Employee();
                emp.setFirstName("Lauri");
                emp.setLastName("Vaadi");
                emp.setEmail("laurom@example.com");
                emp.setAge(28);
                emp.setAddress("Helsinki");
                emp.setDepartment(dept);
                emp.setAccessCard(card);
                emp.setProjects(List.of(proj));

                employeeRepo.save(emp);
            }
        };
    }
}