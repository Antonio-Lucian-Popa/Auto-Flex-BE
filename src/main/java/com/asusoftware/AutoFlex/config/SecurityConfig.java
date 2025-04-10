package com.asusoftware.AutoFlex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/cars/**").hasAnyRole("OWNER", "CLIENT")
                .requestMatchers(HttpMethod.POST, "/api/cars/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.PUT, "/api/cars/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("OWNER")
                .requestMatchers("/api/bookings/**").hasRole("CLIENT")
                .requestMatchers("/api/reviews/**").hasRole("CLIENT")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer().jwt();

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("http://localhost:8080/realms/autoflex");
    }
}