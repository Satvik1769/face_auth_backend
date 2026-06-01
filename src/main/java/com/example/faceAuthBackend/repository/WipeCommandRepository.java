package com.example.faceAuthBackend.repository;

import com.example.faceAuthBackend.domain.WipeCommand;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface WipeCommandRepository extends CrudRepository<WipeCommand, UUID> {

    Optional<WipeCommand> findByUserIdAndConfirmedFalse(UUID userId);

    @Modifying
    @Query("UPDATE wipe_commands SET confirmed = true, confirmed_at = now() WHERE user_id = :userId AND confirmed = false")
    void confirm(UUID userId);
}