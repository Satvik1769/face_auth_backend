package com.example.faceAuthBackend.domain;

import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("users")
@Builder
@With
public record User(
        @Id UUID id,
        String username,
        String passwordHash,
        Instant createdAt,
        Instant deletedAt
) {}