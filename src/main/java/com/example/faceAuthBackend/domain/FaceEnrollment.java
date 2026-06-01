package com.example.faceAuthBackend.domain;

import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("face_enrollments")
@Builder
@With
public record FaceEnrollment(
        @Id UUID userId,
        String embedding,
        int enrollmentVersion,
        Instant enrolledAt,
        Instant syncedAt
) {}