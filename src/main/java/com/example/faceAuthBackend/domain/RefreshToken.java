package com.example.faceAuthBackend.domain;

import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("refresh_tokens")
@Builder
@With
public record RefreshToken(
        @Id UUID id,
        UUID userId,
        String deviceId,
        String tokenHash,
        Instant expiresAt,
        boolean revoked,
        Instant lastSeen,
        String ipAddress,
        Instant createdAt
) {}