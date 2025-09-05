package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.enums.Role;
import com.veribadge.veribadge.dto.KakaoUserInfoDto;
import com.veribadge.veribadge.dto.LoginResponseDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.jwt.JwtGoogleProvider;
import com.veribadge.veribadge.jwt.JwtKakaoProvider;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.service.social.GoogleService;
import com.veribadge.veribadge.service.social.KakaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "소셜 API 관리", description = "카카오, 구글 정보를 받아옵니다.")
@Slf4j
public class AuthController {
    private final JwtKakaoProvider jwtKakaoProvider;
    private final KakaoService kakaoService;
    private final MemberRepository memberRepository;
    private final GoogleService googleService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/kakao/callback")
    public Response<LoginResponseDto> kakaoCallback(@RequestParam String code) {

        String tokenJsonResponse = kakaoService.getAccessToken(code);
        String accessToken = kakaoService.getAccessTokenOnly(tokenJsonResponse);
        log.info(">>>>> ACCESS TOKEN: {} <<<<<", accessToken);
        KakaoUserInfoDto userInfo = kakaoService.getUserInfo(accessToken);
        Long kakaoId = userInfo.getId();

        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .kakaoId(kakaoId)
                        .username(userInfo.getKakaoAccount().getName())
                        .role(Role.USER)
                        .build()));

        String jwt = jwtKakaoProvider.generateToken(member.getUserId());
        forceLogin(member);

        LoginResponseDto responseDto = new LoginResponseDto(member,jwt);
        return Response.success(SuccessStatus.LOGIN_SUCCESS, responseDto);
    }

    private void forceLogin(Member member) {
        UserDetails userDetails = new User(member.getUserId().toString(), "", Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @GetMapping("/google/get-user-info")
    public Map<String, Object> getUserInfo(OAuth2AuthenticationToken authentication) throws IOException {

        // authentication 객체에서 Authorized Client 정보를 로드
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        if (client == null) {
            throw new CustomException(ErrorStatus.INVALID_TOKEN);
            // throw new IllegalStateException("Authorized client not found for the current user.");
        }

        // Authorized Client에서 Access Token 문자열을 추출
        String accessToken = client.getAccessToken().getTokenValue();

        // 추출한 accessToken(문자열)을 서비스 메소드에 전달
        return googleService.getUserInfo(accessToken);
    }
}
