package com.veribadge.veribadge.dto;

import com.veribadge.veribadge.domain.enums.BadgeLevel;
import com.veribadge.veribadge.domain.enums.VerificationStatus;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class MyBadgeResponseDto {
    private String username;
    private String email;
    private VerificationStatus status;

    // 뱃지 발급받은 경우만
    private BadgeLevel badgeLevel;
    private LocalDate badgeDate;

    // 연결 채널 있는 경우만
    private String channelUrl;
    private String badgeTag;

    public MyBadgeResponseDto(String username, String email, VerificationStatus status){
        this.username = username;
        this.email = email;
        this.status = status;
    }
}
