package com.example.rate_limiter.filter;

import com.example.rate_limiter.dto.RateLimitResponse;
import com.example.rate_limiter.factory.RateLimiterFactory;
import com.example.rate_limiter.service.AlgorithmService;
import com.example.rate_limiter.service.MetricsService;
import com.example.rate_limiter.strategy.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final AlgorithmService algorithmService;
    private final RateLimiterFactory rateLimiterFactory;
    private final ObjectMapper objectMapper;
    private final Environment environment;
    private final MetricsService metricsService;
    public RateLimitFilter(AlgorithmService algorithmService,
                           RateLimiterFactory rateLimiterFactory,
                           ObjectMapper objectMapper,Environment environment,MetricsService metricsService) {
        this.algorithmService = algorithmService;
        this.rateLimiterFactory = rateLimiterFactory;
        this.objectMapper = objectMapper;
        this.environment=environment;
        this.metricsService = metricsService;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Current selected algorithm
        String algorithm = request.getHeader("X-Algorithm");

        // Get strategy
        RateLimiter rateLimiter = rateLimiterFactory.getStrategy(algorithm);

        // Client Identifier
        String clientId = request.getRemoteAddr();
        metricsService.incrementTotalRequests();
        // Check Rate Limit (Only Once)
        RateLimitResponse rateLimitResponse =
                rateLimiter.isAllowed(clientId);
        String port = environment.getProperty("local.server.port");

        rateLimitResponse.setServerName(port);
        // Store response for this request only
        request.setAttribute("RATE_LIMIT_RESPONSE", rateLimitResponse);

        // Reject Request
        if (!rateLimitResponse.isAllowed()) {
            metricsService.incrementRejectedRequests();
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(response.getWriter(), rateLimitResponse);

            return;
        }
        metricsService.incrementAllowedRequests();
        // Continue Request
        filterChain.doFilter(request, response);
    }
}