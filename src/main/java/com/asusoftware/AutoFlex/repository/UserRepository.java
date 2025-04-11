package com.asusoftware.AutoFlex.repository;

import com.asusoftware.AutoFlex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKeycloakId(UUID keycloakId);
}
