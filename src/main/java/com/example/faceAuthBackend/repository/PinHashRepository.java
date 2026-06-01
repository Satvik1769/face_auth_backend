package com.example.faceAuthBackend.repository;

import com.example.faceAuthBackend.domain.PinHash;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PinHashRepository extends CrudRepository<PinHash, UUID> {

    Optional<PinHash> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}