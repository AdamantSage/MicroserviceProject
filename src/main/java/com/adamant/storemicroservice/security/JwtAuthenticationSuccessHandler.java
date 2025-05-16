package com.adamant.storemicroservice.security;

import com.adamant.storemicroservice.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationSuccessHandler.class);
    private final JwtService jwtService;

    public JwtAuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 1) Generate the JWT
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        // 2) Log it so you “see” it in your console
        log.info("Form‑login successful for {}; issuing JWT", user.getUsername());
        log.info("Generated JWT: {}", token);

        // 3) (Optional) expose it to the client
        // a) as a response header:
        response.setHeader("Authorization", "Bearer " + token);
        // b) or as an HttpOnly cookie:
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Make sure this matches with HTTPS if using
        cookie.setPath("/");
        response.addCookie(cookie);

        // 4) Redirect on success (you said “take me to index”)
        response.sendRedirect("/");
    }
}
