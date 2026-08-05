package com.example.rate_limiter.strategy;

import com.example.rate_limiter.dto.RateLimitResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("FIXED_WINDOW")
public class FixedWindowRateLimiter implements RateLimiter {

    private static final int REQUEST_LIMIT = 10;
    private static final int WINDOW_SECONDS = 60;

    private final StringRedisTemplate redisTemplate;

    public FixedWindowRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RateLimitResponse isAllowed(String clientId) {

        String key = "fixed:" + clientId;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        boolean allowed = count != null && count <= REQUEST_LIMIT;

        return RateLimitResponse.builder()
                .clientId(clientId)
                .algorithm("FIXED_WINDOW")
                .allowed(allowed)
                .statusCode(allowed ? 200 : 429)
                .message(allowed ? "Request Allowed"
                        : "Rate Limit Exceeded")
                .currentRequestCount(count == null ? 0 : count)
                .requestLimit(REQUEST_LIMIT)
                .windowSizeInSeconds(WINDOW_SECONDS)
                .ttl(ttl == null ? 0 : ttl)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}