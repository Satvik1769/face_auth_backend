package com.example.faceAuthBackend.controller;

import com.example.faceAuthBackend.dto.EnrollmentSyncRequest;
import com.example.faceAuthBackend.service.FaceEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class FaceEnrollmentController {

    private final FaceEnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Instant> sync(@Valid @RequestBody EnrollmentSyncRequest req) {
        Instant syncedAt = enrollmentService.sync(req);
        return Map.of("synced_at", syncedAt);
    }

    @GetMapping("/{userId}")
    public Map<String, List<List<Double>>> get(@PathVariable UUID userId,
                                               @AuthenticationPrincipal UUID tokenUserId) {
        if (!tokenUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access another user's enrollment");
        }
        return Map.of("templates", enrollmentService.getTemplates(userId));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId,
                       @AuthenticationPrincipal UUID tokenUserId) {
        if (!tokenUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete another user's enrollment");
        }
        enrollmentService.delete(userId);
    }
}