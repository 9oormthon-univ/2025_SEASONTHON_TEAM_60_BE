package com.veribadge.veribadge.google;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleService googleService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/me")
    public Map<String, Object> getUserInfo(OAuth2AuthenticationToken authentication) throws IOException {

        // authentication 객체에서 Authorized Client 정보를 로드
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        if (client == null) {
            throw new IllegalStateException("Authorized client not found for the current user.");
        }

        // Authorized Client에서 Access Token 문자열을 추출
        String accessToken = client.getAccessToken().getTokenValue();

        // 추출한 accessToken(문자열)을 서비스 메소드에 전달
        return googleService.getUserInfo(accessToken);
    }
}