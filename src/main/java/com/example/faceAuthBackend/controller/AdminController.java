package com.example.faceAuthBackend.controller;

import com.example.faceAuthBackend.domain.AuthLog;
import com.example.faceAuthBackend.service.AuthLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthLogService authLogService;

    @GetMapping("/users/{id}/auth-history")
    public List<AuthLog> authHistory(@PathVariable UUID id) {
        return authLogService.getHistory(id);
    }
}