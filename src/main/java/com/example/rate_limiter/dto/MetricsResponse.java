package com.example.rate_limiter.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsResponse {

    private long totalRequests;

    private long allowedRequests;

    private long rejectedRequests;
}