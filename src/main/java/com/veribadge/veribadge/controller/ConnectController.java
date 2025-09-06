package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConnectController {

    private final AuthService authService; // 현재 로그인된 사용자를 알려주는 서비스

    /**
     * 구글 계정 연동을 시작하는 API입니다.
     * 이 API는 반드시 우리 서비스의 JWT(Access Token)를 헤더에 포함하여 호출해야 합니다.
     */
    @Operation(
            summary = "Google 계정 연동 시작",
            description = "현재 로그인된 사용자 세션에 유저 ID를 저장한 뒤, Spring Security의 Google OAuth2 인증 페이지로 리디렉션합니다."
    )
    @GetMapping("/auth/connect/google")
    public void connectGoogle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 현재 우리 서비스에 로그인된 사용자가 누구인지 확인합니다. (JWT 기반)
        Member currentMember = authService.getCurrentUser();
        var session = request.getSession(true);
        log.info("connectGoogle JSESSIONID={}", session.getId());

        // 2. 구글 인증하러 가기 전에, 이 사용자의 ID를 세션(임시 메모장)에 기록합니다.
        request.getSession().setAttribute("userIdToConnect", currentMember.getUserId());

        // 3. 이제 진짜 스프링 시큐리티의 구글 인증 페이지로 사용자를 보냅니다.
        response.sendRedirect("/oauth2/authorization/google");
    }
}