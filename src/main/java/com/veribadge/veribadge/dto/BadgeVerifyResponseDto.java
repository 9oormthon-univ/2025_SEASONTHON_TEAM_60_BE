package com.veribadge.veribadge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadgeVerifyResponseDto {
    private String badgeId; // silver, gold, ...
    private boolean valid;  // 존재 여부 (tag + channelUrl 매칭)
}