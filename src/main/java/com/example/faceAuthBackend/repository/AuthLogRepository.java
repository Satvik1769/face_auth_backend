package com.example.faceAuthBackend.repository;

import com.example.faceAuthBackend.domain.AuthLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface AuthLogRepository extends CrudRepository<AuthLog, UUID> {

    @Query("SELECT * FROM auth_logs WHERE user_id = :userId ORDER BY attempted_at DESC")
    List<AuthLog> findByUserIdOrderByAttemptedAtDesc(UUID userId);
}