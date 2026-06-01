package com.example.faceAuthBackend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    @Value("${app.rate-limit.login.capacity:10}")
    private long capacity;

    @Value("${app.rate-limit.login.refill-minutes:1}")
    private long refillMinutes;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket bucketForIp(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillGreedy(capacity, Duration.ofMinutes(refillMinutes))
                        .build())
                .build());
    }
}