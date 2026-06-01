package com.example.faceAuthBackend.controller;

import com.example.faceAuthBackend.config.RateLimitConfig;
import com.example.faceAuthBackend.domain.RefreshToken;
import com.example.faceAuthBackend.dto.*;
import com.example.faceAuthBackend.service.AuthService;
import com.example.faceAuthBackend.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final RateLimitConfig rateLimitConfig;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req,
                                 HttpServletRequest http) {
        return authService.register(req, http.getRemoteAddr());
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req,
                               HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        if (!rateLimitConfig.bucketForIp(ip).tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
        return authService.login(req, ip);
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@Valid @RequestBody RefreshRequest req,
                                       HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        RefreshToken old = authService.consumeAndGetRefreshToken(req.refreshToken(), ip);
        String newAccess = tokenService.generateAccessToken(old.userId());
        String newRefresh = tokenService.issueRefreshToken(old.userId(), old.deviceId(), ip);
        return Map.of("access_token", newAccess, "refresh_token", newRefresh);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest req) {
        authService.logout(req.refreshToken());
    }

    @DeleteMapping("/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@AuthenticationPrincipal UUID userId,
                              @RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password required");
        }
        authService.deleteAccount(userId, password);
    }

    @GetMapping("/sessions")
    public List<SessionView> sessions(@AuthenticationPrincipal UUID userId) {
        return authService.getSessions(userId);
    }

    @DeleteMapping("/sessions/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeSession(@AuthenticationPrincipal UUID userId,
                              @PathVariable String deviceId) {
        authService.revokeSession(userId, deviceId);
    }
}