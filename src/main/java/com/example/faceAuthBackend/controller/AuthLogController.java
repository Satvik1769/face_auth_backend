package com.example.faceAuthBackend.controller;

import com.example.faceAuthBackend.dto.AuthLogEntry;
import com.example.faceAuthBackend.service.AuthLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthLogController {

    private final AuthLogService authLogService;

    @PostMapping("/auth-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, List<UUID>> syncLogs(@RequestBody Map<String, List<AuthLogEntry>> body) {
        List<AuthLogEntry> logs = body.getOrDefault("logs", List.of());
        List<UUID> confirmed = authLogService.syncLogs(logs);
        return Map.of("confirmed_log_ids", confirmed);
    }

    @GetMapping("/wipe-commands")
    public Map<String, Object> checkWipe(@RequestParam UUID userId) {
        AuthLogService.WipeStatus status = authLogService.checkWipe(userId);
        return Map.of(
            "wipe_pending", status.wipePending(),
            "issued_at", status.issuedAt() != null ? status.issuedAt() : "");
    }

    @PostMapping("/wipe-confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmWipe(@RequestBody Map<String, UUID> body) {
        UUID userId = body.get("user_id");
        authLogService.confirmWipe(userId);
    }
}