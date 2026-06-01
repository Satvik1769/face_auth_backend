package com.example.faceAuthBackend.service;

import com.example.faceAuthBackend.domain.RefreshToken;
import com.example.faceAuthBackend.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiry-minutes:15}")
    private int accessTokenExpiryMinutes;

    @Value("${app.jwt.refresh-token-expiry-days:30}")
    private int refreshTokenExpiryDays;

    private final RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(UUID userId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public UUID validateAccessToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String subject = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
        return UUID.fromString(subject);
    }

    public String issueRefreshToken(UUID userId, String deviceId, String ip) {
        byte[] raw = new byte[48];
        new SecureRandom().nextBytes(raw);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);

        Instant now = Instant.now();
        RefreshToken entity = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .deviceId(deviceId)
                .tokenHash(sha256(token))
                .expiresAt(now.plus(refreshTokenExpiryDays, ChronoUnit.DAYS))
                .revoked(false)
                .lastSeen(now)
                .ipAddress(ip)
                .createdAt(now)
                .build();
        refreshTokenRepository.save(entity);
        return token;
    }

    public RefreshToken consume(String rawToken) {
        String hash = sha256(rawToken);
        RefreshToken rt = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new JwtException("Refresh token not found"));
        if (rt.revoked()) throw new JwtException("Refresh token revoked");
        if (rt.expiresAt().isBefore(Instant.now())) throw new JwtException("Refresh token expired");
        refreshTokenRepository.revokeByTokenHash(hash);
        return rt;
    }

    public void revoke(String rawToken) {
        refreshTokenRepository.revokeByTokenHash(sha256(rawToken));
    }

    public String sha256(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}