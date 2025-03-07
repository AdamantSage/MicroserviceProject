package com.adamant.storemicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disabling CSRF protection temporarily
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/products", "/products/create", "/products/edit/**", "products/delete").permitAll()  // Allow all URLs
                .anyRequest().permitAll()  // Allow any other request as well
            );

        return http.build();
    }
}
