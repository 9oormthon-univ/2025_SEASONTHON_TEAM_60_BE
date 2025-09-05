package com.veribadge.veribadge.config;

import com.veribadge.veribadge.handler.CustomOAuth2SuccessHandler;
import com.veribadge.veribadge.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/oauth2/authorization/**",
                                "/auth/**",
                                "/auth/google/get-user-info",
                                "/api/badge/verify",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/",
                                "/images/**",
                                "/login",
                                "/admin/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(customOAuth2SuccessHandler)
                );

        // UsernamePasswordAuthenticationFilter 앞에 JwtAuthenticationFilter를 추가합니다.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
