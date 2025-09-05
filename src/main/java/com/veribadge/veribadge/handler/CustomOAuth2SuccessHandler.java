package com.veribadge.veribadge.handler;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.jwt.JwtGoogleProvider;
import com.veribadge.veribadge.jwt.JwtKakaoProvider;
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

    private final JwtGoogleProvider jwtGoogleProvider;
    private final JwtKakaoProvider jwtKakaoProvider;
    private final GoogleService googleService;
    private final MyBadgeService myBadgeService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        // 1. provider 이름(registrationId) 가져오기
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        String redirectUrl;
        String jwt;

        // 2. provider에 따라 분기 처리
        if ("google".equals(registrationId)) {
            // --- Google 로그인 로직 ---
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();

            Map<String, Object> userInfo = googleService.getUserInfo(accessToken);
            String email = (String) userInfo.get("email");
            String channelUrl = (String) userInfo.get("channelLink");

            myBadgeService.connectChannel(channelUrl, email);

            jwt = jwtGoogleProvider.generateToken(email);

            redirectUrl = "https://veribadge.vercel.app/my-badges";

        } else if ("kakao".equals(registrationId)) {
            // --- Kakao 로그인 로직 ---

            Long userId = Long.valueOf(authentication.getName());
            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));


            jwt = jwtKakaoProvider.generateToken(member.getUserId());

            redirectUrl = "https://veribadge.vercel.app/";
        } else {
            // 지원하지 않는 provider인 경우 예외 처리
            throw new CustomException(ErrorStatus.OAUTH_PROVIDER_NOT_SUPPORTED);
        }

        // 3. --- 공통 로직: JWT 발급 및 리다이렉트 ---
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", jwt)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}