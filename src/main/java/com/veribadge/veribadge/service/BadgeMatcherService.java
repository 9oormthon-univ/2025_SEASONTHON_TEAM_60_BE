package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.BadgeMatch;
import com.veribadge.veribadge.dto.BadgeVerifyResponseDto;
import com.veribadge.veribadge.repository.BadgeMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BadgeMatcherService {

    private final BadgeMatchRepository badgeMatchRepository;

    public BadgeVerifyResponseDto verifyBadgeTag(String tag, String channelUrl) {
        String normalized = normalizeChannelUrl(channelUrl);
        return badgeMatchRepository.findByTagAndChannelUrl(tag, normalized)
                .map(m -> new BadgeVerifyResponseDto(m.getBadgeId(), true))
                .orElseGet(() -> new BadgeVerifyResponseDto("default", false));
    }

    // 전달된 URL을 항상 "www.youtube.com/@핸들" 포맷으로 정규화
    private String normalizeChannelUrl(String input) {
        if (input == null || input.isBlank()) return input;

        // 쿼리스트링 등 제거
        String base = input.split("[?#]", 2)[0];

        // "/@..." 또는 "www.youtube.com/@..." 중 핸들만 추출
        if (base.contains("/@")) {
            String afterAt = base.substring(base.indexOf("/@") + 2);
            String handle = java.net.URLDecoder.decode(afterAt, java.nio.charset.StandardCharsets.UTF_8);
            return "www.youtube.com/@" + handle;
        }

        return base;
    }
}