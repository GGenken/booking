package dev.genken.backend.repository;

import dev.genken.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
}
