package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.dto.BadgeVerifyResponseDto;
import com.veribadge.veribadge.repository.BadgeRepository;
import com.veribadge.veribadge.util.ChannelUrlNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BadgeMatcherService {

    private final BadgeRepository badgeMatchRepository;
    private final ChannelUrlNormalizer channelUrlNormalizer;

    public BadgeVerifyResponseDto verifyBadgeTag(String tag, String channelUrl) {
        String normalized = channelUrlNormalizer.normalize(channelUrl);

        Optional<Badge> matchOpt = badgeMatchRepository.findByVerifiedTagAndChannelUrl(tag, normalized);

        return matchOpt.map(match -> BadgeVerifyResponseDto.builder()
                .badgeLevel(match.getBadgeLevel())
                .valid(true)
                .verifiedDate(match.getVerifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                .build()
        ).orElseGet(() -> BadgeVerifyResponseDto.builder()
                .valid(false)
                .build()
        );
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