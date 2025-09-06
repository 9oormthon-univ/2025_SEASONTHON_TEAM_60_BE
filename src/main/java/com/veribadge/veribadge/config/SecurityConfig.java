package com.veribadge.veribadge.config;

import com.veribadge.veribadge.handler.CustomOAuth2SuccessHandler;
import com.veribadge.veribadge.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin 패턴 설정
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://*.youtube.com",
                "https://www.youtube.com",
                "http://localhost:*",
                "http://127.0.0.1:*",
                "chrome-extension://*",
                "https://veribadge.vercel.app"
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 클라이언트에 노출할 헤더 설정
        configuration.setExposedHeaders(Arrays.asList("*"));

        // 자격 증명(쿠키, 인증 헤더 등) 허용
        configuration.setAllowCredentials(true);

        // Pre-flight 요청의 캐시 시간(초) 설정
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .cors(Customizer.withDefaults())
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
                                "/admin/**",
                                "/main",
                                "/main/**"
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
