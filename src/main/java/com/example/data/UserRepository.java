package com.example.data;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Kirjautumista varten (UserDetailsServiceImpl käyttää sitä jo)
    Optional<User> findByUsername(String username);

    // Kriittinen: rekisteröintiä varten, jotta" käyttäjä on jo olemassa " - tarkistus toimii oikein
    boolean existsByUsername(String username);
}