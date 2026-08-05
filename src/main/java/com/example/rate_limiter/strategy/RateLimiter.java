package com.example.rate_limiter.strategy;

import com.example.rate_limiter.dto.RateLimitResponse;

public interface RateLimiter {
    RateLimitResponse isAllowed(String clientId);
}
