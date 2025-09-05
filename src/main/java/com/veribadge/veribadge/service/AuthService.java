package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository; // MemberRepository 주입

    /**
     * 현재 로그인한 사용자의 Member 엔티티를 반환합니다.
     * @return Member 현재 사용자 엔티티
     */
    public Member getCurrentUser() {
        // 1. SecurityContextHolder에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new CustomException(ErrorStatus.UNAUTHORIZED);
        }


        Long userId = Long.parseLong(authentication.getName());

        // 3. 우리 DB에서 Member 조회
        return memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));
    }
}