package com.adamant.storemicroservice.security;

import com.adamant.storemicroservice.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    
    // Store tokens with their expiration dates
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();
    
    private final JwtService jwtService;
    
    public TokenBlacklistService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Mark this token as revoked.
     */
    public void blacklist(String token) {
        Date expiration = jwtService.extractExpiration(token);
        blacklistedTokens.put(token, expiration);
        log.debug("Token blacklisted, expires at: {}", expiration);
    }

    /**
     * Check if a token has been revoked.
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    /**
     * Scheduled task to remove expired tokens from the blacklist
     * Runs every hour by default
     */
    @Scheduled(fixedRateString = "${security.jwt.blacklist-cleanup-interval:3600000}")
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled cleanup of expired blacklisted tokens");
        Date now = new Date();
        int initialSize = blacklistedTokens.size();
        
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
        
        int removedCount = initialSize - blacklistedTokens.size();
        log.info("Blacklist cleanup complete. Removed {} expired tokens. {} tokens remain.", 
                removedCount, blacklistedTokens.size());
    }
}