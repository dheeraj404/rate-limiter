package com.example.rate_limiter.strategy;

import com.example.rate_limiter.dto.RateLimitResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("TOKEN_BUCKET")
public class TokenBucketRateLimiter implements RateLimiter {

    private static final int CAPACITY = 10;
    private static final double REFILL_RATE = 120.0 / 60.0;

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<List> tokenBucketScript;

    public TokenBucketRateLimiter(
            StringRedisTemplate stringRedisTemplate,
            @Qualifier("tokenBucketScript")
            DefaultRedisScript<List> tokenBucketScript) {

        this.stringRedisTemplate = stringRedisTemplate;
        this.tokenBucketScript = tokenBucketScript;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResponse isAllowed(String clientId) {

        String key = "bucket:" + clientId;

        List<Long> result = (List<Long>) stringRedisTemplate.execute(
                tokenBucketScript,
                Collections.singletonList(key),
                String.valueOf(CAPACITY),
                String.valueOf(REFILL_RATE),
                String.valueOf(System.currentTimeMillis()),
                "1"
        );

        if (result == null || result.isEmpty()) {
            return RateLimitResponse.builder()
                    .allowed(false)
                    .statusCode(500)
                    .message("Unable to execute Lua script")
                    .build();
        }

        return RateLimitResponse.builder()
                .clientId(clientId)
                .algorithm("TOKEN_BUCKET")
                .allowed(result.get(0) == 1)
                .statusCode(result.get(0) == 1 ? 200 : 429)
                .message(result.get(0) == 1
                        ? "Request Allowed"
                        : "Rate Limit Exceeded")
                .remainingTokens(result.get(1))
                .capacity(result.get(2))
                .ttl(result.get(3))
                .refillRate(REFILL_RATE)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}