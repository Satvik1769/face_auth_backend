package com.example.faceAuthBackend.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthLogEntry(
        UUID logId,
        UUID userId,
        Instant attemptedAt,
        String result,
        Float matchScore,
        String failureReason
) {}