package com.veribadge.veribadge.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veribadge.veribadge.dto.KakaoUserInfoDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class KakaoService {

    private final String KAKAO_API_KEY;
    private final String KAKAO_REDIRECT_URI;

    // 생성자를 통해 application.yml의 값을 주입받습니다.
    public KakaoService(
            @Value("${kakao.client-id}") String kakaoApiKey,
            @Value("${kakao.redirect_uri}") String kakaoRedirectUri) {
        this.KAKAO_API_KEY = kakaoApiKey;
        this.KAKAO_REDIRECT_URI = kakaoRedirectUri;
    }

    /**
     * 1. 인가 코드로 카카오 서버에 토큰(Access Token)을 요청합니다.
     * @param code 인가 코드
     * @return 카카오로부터 받은 토큰 정보가 담긴 JSON 문자열
     */
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_API_KEY);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            // 카카오 서버에 POST 요청을 보내 토큰 정보를 받습니다.
            return restTemplate.postForObject("https://kauth.kakao.com/oauth/token", requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            // 카카오 API 연동 중 에러 발생 시 커스텀 예외를 던집니다.
            throw new CustomException(ErrorStatus.KAKAO_API_ERROR);
        }
    }

    /**
     * 2. 토큰 JSON 문자열에서 Access Token 값만 파싱하여 반환합니다.
     * @param tokenJsonResponse 토큰 정보가 담긴 JSON 문자열
     * @return Access Token 문자열
     */
    public String getAccessTokenOnly(String tokenJsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(tokenJsonResponse);
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패 시 커스텀 예외를 던집니다.
            throw new CustomException(ErrorStatus.JSON_PARSING_ERROR);
        }
    }

    /**
     * 3. Access Token을 이용해 카카오 서버에서 사용자 정보를 받아옵니다.
     * @param accessToken Access Token
     * @return 사용자 정보가 담긴 DTO
     */
    public KakaoUserInfoDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        try {
            String rawResponse = restTemplate.postForObject("https://kapi.kakao.com/v2/user/me", requestEntity, String.class);
            log.info(">>>>> KAKAO API RAW RESPONSE: {} <<<<<", rawResponse);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(rawResponse, KakaoUserInfoDto.class);

        } catch (HttpClientErrorException | JsonProcessingException e) { // 두 예외를 모두 잡도록 수정
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            // KAKAO_API_ERROR가 더 포괄적인 의미이므로 그대로 사용해도 좋습니다.
            throw new CustomException(ErrorStatus.KAKAO_API_ERROR);
        }
    }
}