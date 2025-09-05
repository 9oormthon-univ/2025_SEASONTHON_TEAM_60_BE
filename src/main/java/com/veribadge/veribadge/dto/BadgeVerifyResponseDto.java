package com.veribadge.veribadge.dto;

import com.veribadge.veribadge.domain.enums.BadgeLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BadgeVerifyResponseDto {
    private BadgeLevel badgeLevel;   // silver, gold, ...
    private boolean valid;          // 존재 여부 (tag + channelUrl 매칭)
    private String verifiedDate;    // 프론트용 포맷된 문자열
    private String description;
}
