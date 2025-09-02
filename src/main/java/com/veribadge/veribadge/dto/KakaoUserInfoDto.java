package com.veribadge.veribadge.dto;

import lombok.Getter;

@Getter
public class KakaoUserInfoDto {
    private Long id;
    private KakaoAccount kakao_account; // 사용자 계정 정보

    @Getter
    public static class KakaoAccount {
        private String name;
    }
}
