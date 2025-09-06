package com.veribadge.veribadge.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.enums.Role;
import com.veribadge.veribadge.dto.LoginResponseDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.jwt.JwtGoogleProvider;
import com.veribadge.veribadge.jwt.JwtKakaoProvider;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.service.social.GoogleService;
import com.veribadge.veribadge.service.MyBadgeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtGoogleProvider jwtGoogleProvider;
    private final JwtKakaoProvider jwtKakaoProvider;
    private final GoogleService googleService;
    private final MyBadgeService myBadgeService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    private static final String FRONTEND_BASE = "https://veribadge.vercel.app";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        // 1) provider 식별
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        if ("google".equals(registrationId)) {
            // --- 구글: 계정 연동 (토큰 전달 X, 성공 페이지로 이동) ---
            log.info("Google 계정 연동 시작");

            Long currentUserId = (Long) request.getSession().getAttribute("userIdToConnect");
            log.info("Google link target userId={}", currentUserId);
            if (currentUserId == null) throw new CustomException(ErrorStatus.UNAUTHORIZED);
            request.getSession().removeAttribute("userIdToConnect");

            Member currentMember = memberRepository.findById(currentUserId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

            OAuth2AuthorizedClient client =
                    authorizedClientService.loadAuthorizedClient(registrationId, authentication.getName());
            String accessToken = client.getAccessToken().getTokenValue();

            Map<String, Object> userInfo = googleService.getUserInfo(accessToken);
            String email = (String) userInfo.get("email");
            String channelUrl = (String) userInfo.get("channelLink");

            myBadgeService.connectChannel(channelUrl, email, currentMember);

            // 프론트로 리다이렉트 (상태만 알림)
            String redirectUrl = UriComponentsBuilder
                    .fromUriString(FRONTEND_BASE + "/my-badges")
                    .queryParam("linked", "google")
                    .queryParam("status", "success")
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
            return;
        }

        if ("kakao".equals(registrationId)) {
            // --- 카카오: 로그인 (JWT 발급 → 프론트로 전달) ---
            log.info("Kakao 로그인");
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

            Long kakaoId = Long.valueOf(oAuth2User.getName());
            String username = (String) kakaoAccount.get("name"); // 이름만 사용

            Member member = memberRepository.findByKakaoId(kakaoId)
                    .orElseGet(() -> {
                        log.info("신규 카카오 회원. 이름 '{}'으로 자동 가입", username);
                        return memberRepository.save(
                                Member.builder()
                                        .kakaoId(kakaoId)
                                        .username(username)
                                        .role(Role.USER)
                                        .build()
                        );
                    });

            String jwt = jwtKakaoProvider.generateToken(member.getUserId());

            // 토큰은 프래그먼트(#)로 전달 → 리퍼러/로그에 남을 가능성 낮춤
            String fragment = "token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8)
                    + "&provider=kakao";

            String redirectUrl = UriComponentsBuilder
                    .fromUriString(FRONTEND_BASE + "/auth/google/callback")
                    .fragment(fragment)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
            return;
        }

        // 지원하지 않는 provider
        throw new CustomException(ErrorStatus.BAD_REQUEST);
    }
}
