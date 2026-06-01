package com.example.faceAuthBackend.service;

import com.example.faceAuthBackend.domain.PinHash;
import com.example.faceAuthBackend.domain.User;
import com.example.faceAuthBackend.repository.PinHashRepository;
import com.example.faceAuthBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class PinService {

    private final PinHashRepository pinHashRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void setup(UUID userId, String pinHash) {
        pinHashRepo.findByUserId(userId).ifPresentOrElse(
                existing -> pinHashRepo.save(existing.withPinHash(pinHash).withUpdatedAt(Instant.now())),
                () -> pinHashRepo.save(PinHash.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .pinHash(pinHash)
                        .updatedAt(Instant.now())
                        .build())
        );
    }

    public boolean verify(UUID userId, String pinHash) {
        return pinHashRepo.findByUserId(userId)
                .map(p -> p.pinHash().equals(pinHash))
                .orElse(false);
    }

    @Transactional
    public void reset(UUID userId, String password, String newPinHash) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(password, user.passwordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }
        setup(userId, newPinHash);
    }
}