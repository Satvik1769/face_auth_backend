package com.example.faceAuthBackend.repository;

import com.example.faceAuthBackend.domain.FaceEnrollment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FaceEnrollmentRepository extends CrudRepository<FaceEnrollment, UUID> {}