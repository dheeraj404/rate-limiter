package com.example.rate_limiter.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final StringRedisTemplate redisTemplate;

    public MetricsService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementTotalRequests() {
        redisTemplate.opsForValue().increment("metrics:total_requests");
    }

    public void incrementAllowedRequests() {
        redisTemplate.opsForValue().increment("metrics:allowed_requests");
    }

    public void incrementRejectedRequests() {
        redisTemplate.opsForValue().increment("metrics:rejected_requests");
    }

    public long getTotalRequests() {
        String value = redisTemplate.opsForValue()
                .get("metrics:total_requests");

        return value == null ? 0 : Long.parseLong(value);
    }

    public long getAllowedRequests() {
        String value = redisTemplate.opsForValue()
                .get("metrics:allowed_requests");

        return value == null ? 0 : Long.parseLong(value);
    }

    public long getRejectedRequests() {
        String value = redisTemplate.opsForValue()
                .get("metrics:rejected_requests");

        return value == null ? 0 : Long.parseLong(value);
    }

}
