package com.example.rate_limiter.controller;

import com.example.rate_limiter.dto.MetricsResponse;
import com.example.rate_limiter.service.MetricsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(origins = "http://localhost:4200")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    public MetricsResponse getMetrics() {

        return MetricsResponse.builder()
                .totalRequests(metricsService.getTotalRequests())
                .allowedRequests(metricsService.getAllowedRequests())
                .rejectedRequests(metricsService.getRejectedRequests())
                .build();
    }

}