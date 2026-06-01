package com.example.faceAuthBackend.repository;

import com.example.faceAuthBackend.domain.RefreshToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);

    @Modifying
    @Query("UPDATE refresh_tokens SET revoked = true WHERE token_hash = :tokenHash")
    void revokeByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE refresh_tokens SET revoked = true WHERE user_id = :userId AND device_id = :deviceId")
    void revokeByUserIdAndDeviceId(UUID userId, String deviceId);

    @Modifying
    @Query("UPDATE refresh_tokens SET last_seen = now(), ip_address = :ip WHERE token_hash = :tokenHash")
    void updateLastSeen(String tokenHash, String ip);
}