package ru.seleznev.springdata;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.entities.AuthUser;

import java.util.Optional;

public interface SpringDataAuthUserRepository extends JpaRepository<AuthUser, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<AuthUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
