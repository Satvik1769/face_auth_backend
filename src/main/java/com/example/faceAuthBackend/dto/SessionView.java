package com.example.faceAuthBackend.dto;

import java.time.Instant;

public record SessionView(String deviceId, Instant lastSeen, String ip) {}