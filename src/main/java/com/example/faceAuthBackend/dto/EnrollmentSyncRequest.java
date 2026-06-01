package com.example.faceAuthBackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EnrollmentSyncRequest(
        @NotNull UUID userId,
        @NotNull @NotEmpty List<Double> embedding,
        @NotNull Instant enrolledAt,
        @Positive int enrollmentVersion
) {}