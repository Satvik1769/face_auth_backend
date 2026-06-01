package com.example.faceAuthBackend.service;

import com.example.faceAuthBackend.domain.AuthLog;
import com.example.faceAuthBackend.domain.WipeCommand;
import com.example.faceAuthBackend.dto.AuthLogEntry;
import com.example.faceAuthBackend.repository.AuthLogRepository;
import com.example.faceAuthBackend.repository.WipeCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthLogService {

    private final AuthLogRepository authLogRepo;
    private final WipeCommandRepository wipeCommandRepo;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<UUID> syncLogs(List<AuthLogEntry> entries) {
        for (AuthLogEntry entry : entries) {
            jdbcTemplate.update(
                "INSERT INTO auth_logs (log_id, user_id, attempted_at, result, match_score, failure_reason) " +
                "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (log_id) DO NOTHING",
                entry.logId(),
                entry.userId(),
                entry.attemptedAt() != null ? Timestamp.from(entry.attemptedAt()) : null,
                entry.result(),
                entry.matchScore(),
                entry.failureReason());
        }
        return entries.stream().map(AuthLogEntry::logId).toList();
    }

    public record WipeStatus(boolean wipePending, Instant issuedAt) {}

    public WipeStatus checkWipe(UUID userId) {
        return wipeCommandRepo.findByUserIdAndConfirmedFalse(userId)
                .map(w -> new WipeStatus(true, w.issuedAt()))
                .orElse(new WipeStatus(false, null));
    }

    @Transactional
    public void confirmWipe(UUID userId) {
        wipeCommandRepo.confirm(userId);
    }

    public List<AuthLog> getHistory(UUID userId) {
        return authLogRepo.findByUserIdOrderByAttemptedAtDesc(userId);
    }
}