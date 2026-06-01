package com.example.faceAuthBackend.service;

import com.example.faceAuthBackend.dto.EnrollmentSyncRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FaceEnrollmentService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Instant sync(EnrollmentSyncRequest req) {
        Instant now = Instant.now();
        String embeddingJson;
        try {
            embeddingJson = objectMapper.writeValueAsString(req.embedding());
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Invalid embedding data");
        }

        // Only overwrite when incoming version is strictly higher than stored version
        try {
            Integer existingVersion = jdbcTemplate.queryForObject(
                "SELECT enrollment_version FROM face_enrollments WHERE user_id = ?",
                Integer.class, req.userId());
            if (existingVersion != null && existingVersion >= req.enrollmentVersion()) {
                Timestamp ts = jdbcTemplate.queryForObject(
                    "SELECT synced_at FROM face_enrollments WHERE user_id = ?",
                    Timestamp.class, req.userId());
                return ts != null ? ts.toInstant() : now;
            }
        } catch (EmptyResultDataAccessException ignored) {}

        jdbcTemplate.update(
            "INSERT INTO face_enrollments (user_id, embedding, enrollment_version, enrolled_at, synced_at) " +
            "VALUES (?, ?, ?, ?, ?) " +
            "ON CONFLICT (user_id) DO UPDATE SET " +
            "embedding = EXCLUDED.embedding, " +
            "enrollment_version = EXCLUDED.enrollment_version, " +
            "enrolled_at = EXCLUDED.enrolled_at, " +
            "synced_at = EXCLUDED.synced_at",
            req.userId(), embeddingJson, req.enrollmentVersion(),
            Timestamp.from(req.enrolledAt()), Timestamp.from(now));
        return now;
    }

    public List<List<Double>> getTemplates(UUID userId) {
        try {
            String embeddingJson = jdbcTemplate.queryForObject(
                "SELECT embedding FROM face_enrollments WHERE user_id = ?",
                (rs, rowNum) -> rs.getString(1),
                userId);
            if (embeddingJson == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No enrollment found");
            }
            List<Double> floats = objectMapper.readValue(embeddingJson, new TypeReference<>() {});
            return List.of(floats);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No enrollment found");
        } catch (JacksonException e) {
            throw new IllegalStateException("Corrupt embedding data", e);
        }
    }

    @Transactional
    public void delete(UUID userId) {
        jdbcTemplate.update("DELETE FROM face_enrollments WHERE user_id = ?", userId);
    }
}