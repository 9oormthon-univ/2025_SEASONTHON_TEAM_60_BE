package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Member; // Member 엔티티 import
import com.veribadge.veribadge.dto.KakaoUserInfoDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.MemberRepository; // MemberRepository import
import com.veribadge.veribadge.service.social.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoService kakaoService;
    private final MemberRepository memberRepository; // MemberRepository 주입
    private final HttpServletRequest httpServletRequest;

    /**
     * 현재 로그인한 사용자의 Member 엔티티를 반환합니다.
     * @return Member 현재 사용자 엔티티
     */
    public Member getCurrentUser() {
        // 1. SecurityContextHolder에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.INVALID_TOKEN);
        }

        // 2. JWT의 subject(email)을 꺼내기
        Long userId = Long.valueOf(authentication.getName());

        // 3. 우리 DB에서 Member 조회
        return memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));
    }
}