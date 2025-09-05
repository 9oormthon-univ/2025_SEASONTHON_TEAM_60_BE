package com.veribadge.veribadge.handler;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.jwt.JwtProvider;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.service.social.GoogleService;
import com.veribadge.veribadge.service.MyBadgeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final GoogleService googleService;
    private final MyBadgeService myBadgeService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        // 1. 구글로부터 Access Token 가져오기
        String accessToken = client.getAccessToken().getTokenValue();

        // 2. GoogleService를 호출하여 이메일과 채널 정보 가져오기 (수정된 메소드 호출)
        Map<String, Object> userInfo = googleService.getUserInfo(accessToken);
        String email = (String) userInfo.get("email");
        String channelUrl = (String) userInfo.get("channelLink");

        // 3. DB에 Badge 정보 업데이트 (핵심 로직 추가)
        // !! 중요 !!
        // 여기서 '누구'의 배지를 업데이트할지 결정해야 합니다.
        // 예를 들어, 구글 이메일(email)을 통해 우리 시스템의 사용자(Verification)를 찾고
        // 그 사용자의 badge를 업데이트 해야 합니다.
        // 아래는 'verificationId'를 어떻게든 가져왔다고 가정한 예시입니다.
        // Long verificationId = getVerificationIdFromSomewhere(email);
        // badgeService.updateBadgeWithGoogleInfo(verificationId, channelUrl, email);
        myBadgeService.connectChannel(channelUrl, email);


        // 4. JWT 발급
        Long userId = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));


        String jwt = jwtProvider.generateToken(member.getUserId());

        // 5. 프론트로 JWT 내려주기
        String targetUrl = UriComponentsBuilder.fromUriString("https://veribadge.vercel.app/my-badges")
                .queryParam("token", jwt)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}