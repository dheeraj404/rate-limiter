package com.example.rate_limiter.factory;

import com.example.rate_limiter.strategy.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class RateLimiterFactory {
    private final Map<String, RateLimiter> strategies;

    public RateLimiterFactory(Map<String, RateLimiter> strategies) {
        this.strategies = strategies;
    }

    public RateLimiter getStrategy(String algorithm) {
        return strategies.get(algorithm);
    }
}
