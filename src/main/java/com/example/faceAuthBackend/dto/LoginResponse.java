package com.example.faceAuthBackend.dto;

import java.util.UUID;

public record LoginResponse(UUID userId, String token) {}