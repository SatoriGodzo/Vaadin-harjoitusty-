package com.example;

import com.example.data.*;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            EmployeeRepository employeeRepo,
            DepartmentRepository deptRepo,
            ProjectRepository projectRepo,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // 1. Создаем трех разных пользователей по заданию
            if (userRepo.count() == 0) {
                // Админ
                User admin = new User();
                admin.setUsername("Admin");
                admin.setHashedValue(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(Role.ADMIN));
                userRepo.save(admin);

                // Обычный юзер
                User user = new User();
                user.setUsername("us");
                user.setHashedValue(passwordEncoder.encode("us"));
                user.setRoles(Set.of(Role.USER));
                userRepo.save(user);

                // Супер юзер
                User superUser = new User();
                superUser.setUsername("sup");
                superUser.setHashedValue(passwordEncoder.encode("sup"));
                superUser.setRoles(Set.of(Role.SUPER));
                userRepo.save(superUser);
            }

            // 2. Тестовые данные для таблиц
            if (employeeRepo.count() == 0) {
                Department dept = new Department();
                dept.setName("IT Support");
                dept.setOffice("HQ-01");
                dept.setContactEmail("it-dept@example.com");
                dept.setBudgetCode("B-100");
                deptRepo.save(dept);

                Project proj = new Project();
                proj.setProjectName("AI Integration");
                proj.setClientName("Google");
                proj.setBudget(50000.0);
                proj.setDeadline(LocalDate.now().plusMonths(6));
                projectRepo.save(proj);

                Employee emp = new Employee();
                emp.setFirstName("Lauri");
                emp.setLastName("Vaadi");
                emp.setEmail("lauri.vaadi@example.com");
                emp.setAge(30);
                emp.setAddress("Helsinki, Main str 1");
                emp.setDepartment(dept);
                emp.setProjects(List.of(proj));

                employeeRepo.save(emp);
                System.out.println(">>> Данные созданы: Admin, User, Super + тестовый сотрудник.");
            }
        };
    }
}