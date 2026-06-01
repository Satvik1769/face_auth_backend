package com.example.faceAuthBackend.domain;

import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("pin_hashes")
@Builder
@With
public record PinHash(
        @Id UUID id,
        UUID userId,
        String pinHash,
        Instant updatedAt
) {}