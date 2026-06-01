package com.example.faceAuthBackend.service;

import com.example.faceAuthBackend.domain.RefreshToken;
import com.example.faceAuthBackend.domain.User;
import com.example.faceAuthBackend.dto.*;
import com.example.faceAuthBackend.repository.RefreshTokenRepository;
import com.example.faceAuthBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest req, String ip) {
        if (userRepo.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        User user = User.builder()
                .id(randomUUID())
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .createdAt(Instant.now())
                .deletedAt(null)
                .build();
        user = userRepo.save(user);

        String access = tokenService.generateAccessToken(user.id());
        String refresh = tokenService.issueRefreshToken(user.id(), req.deviceId(), ip);
        return new AuthResponse(user.id(), access, refresh);
    }

    @Transactional
    public LoginResponse login(LoginRequest req, String ip) {
        User user = userRepo.findByUsername(req.username())
                .filter(u -> u.deletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.passwordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = tokenService.generateAccessToken(user.id());
        return new LoginResponse(user.id(), token);
    }

    @Transactional
    public RefreshToken consumeAndGetRefreshToken(String rawRefreshToken, String ip) {
        return tokenService.consume(rawRefreshToken);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        tokenService.revoke(rawRefreshToken);
    }

    @Transactional
    public void deleteAccount(UUID userId, String password) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(password, user.passwordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }
        userRepo.softDelete(userId);
    }

    public List<SessionView> getSessions(UUID userId) {
        return refreshTokenRepo.findByUserIdAndRevokedFalse(userId).stream()
                .map(rt -> new SessionView(rt.deviceId(), rt.lastSeen(), rt.ipAddress()))
                .toList();
    }

    @Transactional
    public void revokeSession(UUID userId, String deviceId) {
        refreshTokenRepo.revokeByUserIdAndDeviceId(userId, deviceId);
    }
}