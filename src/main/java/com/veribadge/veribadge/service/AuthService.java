package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Member; // Member 엔티티 import
import com.veribadge.veribadge.dto.KakaoUserInfoDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.MemberRepository; // MemberRepository import
import com.veribadge.veribadge.service.social.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
        // 1. 헤더에서 Access Token 추출
        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorStatus.INVALID_TOKEN);
        }
        String accessToken = authorizationHeader.substring(7);

        // 2. Access Token으로 카카오에서 사용자 정보 조회
        KakaoUserInfoDto kakaoUserInfo = kakaoService.getUserInfo(accessToken);
        Long kakaoId = kakaoUserInfo.getId();

        // 3. 카카오 ID로 우리 DB에서 회원 조회
        //    orElseThrow()를 사용하여 해당 회원이 없으면 예외를 발생시킵니다.
        return memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND)); // 적절한 예외를 정의하여 사용
    }
}