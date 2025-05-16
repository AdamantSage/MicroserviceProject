package com.adamant.storemicroservice.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(JwtLogoutSuccessHandler.class);
    private final TokenBlacklistService blacklistService;

    public JwtLogoutSuccessHandler(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        // 1) Invalidate the HTTP session (so your inactivity logic still works)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.debug("HTTP session invalidated on logout");
        }

        // 2) Extract token from cookie (or header)
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("JWT".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }

        // 3) Blacklist the JWT
        if (token != null) {
            blacklistService.blacklist(token);
            log.info("JWT token blacklisted on logout: {}", token);

            // Send token back to client for debugging/logging
            response.addHeader("X-Blacklisted-JWT", token);
        } else {
            log.warn("Logout with no JWT cookie present");
        }

        // 4) Clear the JWT cookie
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 5) Redirect to login page
        response.sendRedirect("/login?logout");
    }
}
