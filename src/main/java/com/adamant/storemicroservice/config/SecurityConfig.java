package com.adamant.storemicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disabling CSRF protection temporarily
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/home","/register","/login", "/products", "/products/create", "/products/edit/**", "products/delete").permitAll()  // Allow all URLs
                .anyRequest().permitAll()  // Allow any other request as well
            )
            .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/login?loginSuccess=true",true)
            .permitAll()
            )
            .logout(logout -> logout
            .logoutSuccessUrl("/login?logout=true")
            .permitAll()
        );
        return http.build();
    }
}
