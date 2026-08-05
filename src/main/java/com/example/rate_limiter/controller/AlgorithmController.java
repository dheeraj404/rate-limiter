package com.example.rate_limiter.controller;

import com.example.rate_limiter.dto.AlgorithmRequest;
import com.example.rate_limiter.dto.RateLimitResponse;
import com.example.rate_limiter.service.AlgorithmService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "http://localhost:4200")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @PostMapping("/algorithm")
    public ResponseEntity<String> changeAlgorithm(
            @RequestBody AlgorithmRequest request) {

        algorithmService.setAlgorithm(request.getAlgorithm());

        return ResponseEntity.ok(
                "Current Algorithm : " + request.getAlgorithm());
    }

    @GetMapping("/algorithm")
    public ResponseEntity<String> getAlgorithm() {
        return ResponseEntity.ok(algorithmService.getAlgorithm());
    }
    @GetMapping("/test")
    public RateLimitResponse test(HttpServletRequest request) {

        return (RateLimitResponse)
                request.getAttribute("RATE_LIMIT_RESPONSE");
    }
}
