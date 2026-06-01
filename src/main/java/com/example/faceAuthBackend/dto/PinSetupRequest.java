package com.example.faceAuthBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PinSetupRequest(@NotBlank String pinHash, @NotNull UUID userId) {}