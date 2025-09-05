package com.veribadge.veribadge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class KakaoUserInfoDto {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount; // 사용자 계정 정보
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class KakaoAccount {
        private String name;
    }
}
