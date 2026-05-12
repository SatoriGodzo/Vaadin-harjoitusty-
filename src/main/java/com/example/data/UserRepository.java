package com.example.data;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Для логина (UserDetailsServiceImpl его уже использует)
    Optional<User> findByUsername(String username);

    // КРИТИЧНО: Для регистрации, чтобы проверка "User already exists" работала корректно
    boolean existsByUsername(String username);
}