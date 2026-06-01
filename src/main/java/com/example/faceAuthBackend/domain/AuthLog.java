package com.example.faceAuthBackend.domain;

import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("auth_logs")
@Builder
@With
public record AuthLog(
        @Id UUID logId,
        UUID userId,
        Instant attemptedAt,
        String result,
        Float matchScore,
        String failureReason,
        Instant receivedAt
) {}