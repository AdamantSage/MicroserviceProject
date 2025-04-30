package com.adamant.storemicroservice.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private final int maxRequests;
    private final long timeWindowMillis;
    private final Map<String, UserRequestInfo> requestCounts = new ConcurrentHashMap<>();

    public RateLimiter(int maxRequests, long timeWindowMillis) {
        this.maxRequests = maxRequests;
        this.timeWindowMillis = timeWindowMillis;
    }

    public boolean allowRequest(String clientIp) {
        UserRequestInfo info = requestCounts.getOrDefault(clientIp, new UserRequestInfo(0, Instant.now().toEpochMilli()));
        long now = Instant.now().toEpochMilli();

        if (now - info.timestamp > timeWindowMillis) {
            // Reset the window
            info = new UserRequestInfo(1, now);
        } else {
            info.count++;
        }

        requestCounts.put(clientIp, info);
        return info.count <= maxRequests;
    }

    private static class UserRequestInfo {
        int count;
        long timestamp;

        UserRequestInfo(int count, long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }
}
