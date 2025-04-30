package com.adamant.storemicroservice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiter rateLimiter = new RateLimiter(100, 60_000); // 100 requests/min

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
    
        if (!rateLimiter.allowRequest(clientIp)) {
            response.setStatus(429); // Too Many Requests
            response.setHeader("Retry-After", "60");
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return false;
        }
    
        return true;
    }
    
}
