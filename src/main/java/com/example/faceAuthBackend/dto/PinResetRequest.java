package com.example.faceAuthBackend.dto;

import jakarta.validation.constraints.NotBlank;

public record PinResetRequest(@NotBlank String password, @NotBlank String newPinHash) {}