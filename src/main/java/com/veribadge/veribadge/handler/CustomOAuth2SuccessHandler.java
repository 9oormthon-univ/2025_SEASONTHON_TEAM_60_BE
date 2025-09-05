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
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        // 1. provider 이름(registrationId) 가져오기
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        Response<?> finalResponse;
        //String redirectUrl;
        String jwt;

        Member finalMember; // 최종적으로 로그인된 회원을 담을 변수

               // 2. provider에 따라 분기 처리
        if ("google".equals(registrationId)) {
            log.info("Google 계정 연동을 시작");

            // 1. 세션(임시 메모장)에서 '누가' 연동을 시작했는지 사용자 ID를 꺼냅니다.
            Long currentUserId = (Long) request.getSession().getAttribute("userIdToConnect");
            log.info("Google link target userId={}", currentUserId);
            log.info("SuccessHandler JSESSIONID={}",
                    request.getSession(false) != null ? request.getSession(false).getId() : "null");
            if (currentUserId == null) {
                throw new CustomException(ErrorStatus.UNAUTHORIZED); // 비정상적인 접근
            }
            request.getSession().removeAttribute("userIdToConnect"); // 사용 후 메모는 바로 삭제

            Member currentMember = memberRepository.findById(currentUserId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));


            // --- Google 로그인 로직 ---
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(registrationId, authentication.getName());
            String accessToken = client.getAccessToken().getTokenValue();

            Map<String, Object> userInfo = googleService.getUserInfo(accessToken);
            String email = (String) userInfo.get("email");
            String channelUrl = (String) userInfo.get("channelLink");

            myBadgeService.connectChannel(channelUrl, email, currentMember);

            finalResponse = Response.success(SuccessStatus.CHANNEL_CONNECTED, Map.of("message", "구글 계정이 성공적으로 연동되었습니다."));

            //finalResponse = Response.success(SuccessStatus.CHANNEL_CONNECTED, Map.of("accessToken", accessToken));
            //jwt = jwtGoogleProvider.generateToken(email);
            //redirectUrl = "https://veribadge.vercel.app/my-badges";

        } else if ("kakao".equals(registrationId)) {
            // --- Kakao 로그인 로직 ---
            log.info("Kakao 로그인");
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

            Long kakaoId = Long.valueOf(oAuth2User.getName());
            String username = (String) kakaoAccount.get("name");

            Member member = memberRepository.findByKakaoId(kakaoId)
                    .orElseGet(() -> {
                        log.info("신규 카카오 회원입니다. 이름 '{}'(으)로 자동 가입합니다.", username);
                        return memberRepository.save(Member.builder()
                                .kakaoId(kakaoId)
                                .username(username)
                                .role(Role.USER)
                                .build());
                    });

            jwt = jwtKakaoProvider.generateToken(member.getUserId());
            LoginResponseDto loginResponseDto = new LoginResponseDto(member, jwt);
            finalResponse = Response.success(SuccessStatus.LOGIN_SUCCESS, loginResponseDto);
            //redirectUrl = "https://veribadge.vercel.app/";
        } else {
            // 지원하지 않는 provider인 경우 예외 처리
            throw new CustomException(ErrorStatus.BAD_REQUEST);
        }

        // 3. --- 공통 로직: JWT 발급 및 리다이렉트 ---
        /*
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", jwt)
                .build().toUriString();

        response.sendRedirect(targetUrl);
         */





        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String result = objectMapper.writeValueAsString(finalResponse);
        response.getWriter().write(result);
        // ------------------------------------


    }
}