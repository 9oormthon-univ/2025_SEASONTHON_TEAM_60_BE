package com.veribadge.veribadge.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtGoogleProvider jwtGoogleProvider;
    private final JwtKakaoProvider jwtKakaoProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 1. 구글 토큰인지 먼저 검증
            if (jwtGoogleProvider.validateToken(token)) {
                String email = jwtGoogleProvider.getEmail(token);
                setAuthentication(email, request);

            // 2. 구글 토큰이 아니라면 카카오 토큰인지 검증
            } else if (jwtKakaoProvider.validateToken(token)) {
                Long userId = jwtKakaoProvider.getUserId(token);
                setAuthentication(userId, request);
            }
        }

        filterChain.doFilter(request, response);
    }

    // 공통 로직을 별도 메서드로 분리
    private void setAuthentication(Object principal, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, null);

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}