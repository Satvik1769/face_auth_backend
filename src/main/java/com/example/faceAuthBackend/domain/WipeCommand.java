package com.example.faceAuthBackend.domain;

import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("wipe_commands")
@Builder
@With
public record WipeCommand(
        @Id UUID id,
        UUID userId,
        Instant issuedAt,
        boolean confirmed,
        Instant confirmedAt
) {}