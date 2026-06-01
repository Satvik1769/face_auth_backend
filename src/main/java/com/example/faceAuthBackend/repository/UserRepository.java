package com.example.faceAuthBackend.repository;

import com.example.faceAuthBackend.domain.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE users SET deleted_at = now() WHERE id = :id AND deleted_at IS NULL")
    void softDelete(UUID id);
}