package com.example.rate_limiter.service;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {
    private volatile String currentAlgorithm = "TOKEN_BUCKET";

    public void setAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }

    public String getAlgorithm() {
        return currentAlgorithm;
    }
}
