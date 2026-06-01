package com.example.faceAuthBackend.controller;

import com.example.faceAuthBackend.dto.*;
import com.example.faceAuthBackend.service.PinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/pin")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    @PostMapping("/setup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Boolean> setup(@Valid @RequestBody PinSetupRequest req,
                                      @AuthenticationPrincipal UUID userId) {
        pinService.setup(req.userId(), req.pinHash());
        return Map.of("ok", true);
    }

    @PostMapping("/verify")
    public Map<String, Boolean> verify(@Valid @RequestBody PinVerifyRequest req) {
        boolean valid = pinService.verify(req.userId(), req.pinHash());
        return Map.of("valid", valid);
    }

    @PostMapping("/reset")
    public Map<String, Boolean> reset(@Valid @RequestBody PinResetRequest req,
                                      @AuthenticationPrincipal UUID userId) {
        pinService.reset(userId, req.password(), req.newPinHash());
        return Map.of("ok", true);
    }
}