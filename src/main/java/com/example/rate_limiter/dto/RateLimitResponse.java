package com.example.rate_limiter.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitResponse {

    /* Request Information */
    private String clientId;
    private String endpoint;
    private String algorithm;
    private String serverName;

    /* Request Result */
    private boolean allowed;
    private int statusCode;
    private String message;

    /* Token Bucket */
    private long remainingTokens;
    private long capacity;
    private double refillRate;

    /* Fixed/Sliding Window */
    private long currentRequestCount;
    private long requestLimit;
    private long windowSizeInSeconds;

    /* Common */
    private long ttl;
    private long timestamp;
    private long processingTimeMs;
}