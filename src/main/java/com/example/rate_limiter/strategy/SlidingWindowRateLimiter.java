package com.example.rate_limiter.strategy;

import com.example.rate_limiter.dto.RateLimitResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("SLIDING_WINDOW")
public class SlidingWindowRateLimiter implements RateLimiter {

    private static final int REQUEST_LIMIT = 10;
    private static final int WINDOW_SIZE_MS = 60_000;

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<List> slidingWindowScript;

    public SlidingWindowRateLimiter(
            StringRedisTemplate stringRedisTemplate,
            @Qualifier("slidingWindowScript")
            DefaultRedisScript<List> slidingWindowScript) {

        this.stringRedisTemplate = stringRedisTemplate;
        this.slidingWindowScript = slidingWindowScript;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResponse isAllowed(String clientId) {

        String key = "window:" + clientId;

        List<Long> result =
                (List<Long>) stringRedisTemplate.execute(
                        slidingWindowScript,
                        Collections.singletonList(key),
                        String.valueOf(System.currentTimeMillis()),
                        String.valueOf(WINDOW_SIZE_MS),
                        String.valueOf(REQUEST_LIMIT)
                );

        if (result == null || result.isEmpty()) {

            return RateLimitResponse.builder()
                    .allowed(false)
                    .statusCode(500)
                    .message("Lua Script Failed")
                    .build();
        }

        return RateLimitResponse.builder()
                .clientId(clientId)
                .algorithm("SLIDING_WINDOW")
                .allowed(result.get(0) == 1)
                .statusCode(result.get(0) == 1 ? 200 : 429)
                .message(result.get(0) == 1
                        ? "Request Allowed"
                        : "Rate Limit Exceeded")
                .currentRequestCount(result.get(1))
                .requestLimit(result.get(2))
                .windowSizeInSeconds(WINDOW_SIZE_MS / 1000)

                .ttl(result.get(3))
                .timestamp(System.currentTimeMillis())
                .build();
    }
}