package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.dto.BadgeVerifyResponseDto;
import com.veribadge.veribadge.dto.CommentTagVerifyRequestDto;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.service.BadgeMatcherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 유튜브 댓글을 분석하고 해당 유저에게 뱃지를 매칭하는 API 컨트롤러
 */
@CrossOrigin(origins = "https://www.youtube.com")
@RestController
@RequestMapping("/api/badge")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeMatcherService badgeMatcherService;

    /**
     * 프론트에서 { tag, channelUrl }을 전달하면,
     * DB에서 두 조건이 일치하는지 확인 후 badgeId + valid 여부 반환
     */
    @PostMapping("/verify")
    public ResponseEntity<Response<BadgeVerifyResponseDto>> verifyBadgeTag(
            @Valid @RequestBody CommentTagVerifyRequestDto request) {

        BadgeVerifyResponseDto result =
                badgeMatcherService.verifyBadgeTag(request.getTag(), request.getChannelUrl());

        return ResponseEntity.ok(Response.success(SuccessStatus.BADGE_MATCH_SUCCESS, result));
    }
}