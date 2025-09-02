package com.veribadge.veribadge.dto;

import com.veribadge.veribadge.domain.enums.BadgeLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadgeVerifyResponseDto {
    private BadgeLevel badgeLevel; // silver, gold, ...
    private boolean valid;  // 존재 여부 (tag + channelUrl 매칭)
}