package com.example.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Tarkennettu haku: (etunimi tai sukunimi tai sähköpostiosoite) ja osasto ja ajanjakso.
     */
    public List<Employee> findByAdvancedFilter(String searchTerm, String deptName, LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);

        List<Predicate> predicates = new ArrayList<>();

        // 1. Monimutkainen ehto (X tai Y tai Z) - tehtävän kohta 1 ja 5
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String lowerTerm = "%" + searchTerm.toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(employee.get("firstName")), lowerTerm);
            Predicate lastNamePredicate = cb.like(cb.lower(employee.get("lastName")), lowerTerm);
            Predicate emailPredicate = cb.like(cb.lower(employee.get("email")), lowerTerm);

            predicates.add(cb.or(namePredicate, lastNamePredicate, emailPredicate));
        }

        // 2. JOIN Department (haku  LIKE) - tehtävän 3 ja 4 kohta
        if (deptName != null && !deptName.isEmpty()) {
            //  JoinType.LEFT, jotta työntekijät, joilla ei ole osastoja, eivät poistu asiasta
            Join<Employee, Department> departmentJoin = employee.join("department", JoinType.LEFT);
            String deptLike = "%" + deptName.toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(departmentJoin.get("name")), deptLike));
        }

        // 3. JOIN Kortti + Päivämäärähaku-tehtävän kohta 2
        if (startDate != null && endDate != null) {
            Join<Employee, AccessCard> cardJoin = employee.join("accessCard", JoinType.LEFT);
            predicates.add(cb.between(cardJoin.get("issuedDate"), startDate, endDate));
        }

        // Kokoonpano: lisäämme ehtoja vain, jos käyttäjä on syöttänyt niitä
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(query).getResultList();
    }
}