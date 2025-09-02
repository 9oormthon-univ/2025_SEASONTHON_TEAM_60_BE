package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.BadgeLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadgeMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유효한 @veri-태그 원문 (예: @veri-gold-ab12cd)
    @Column(nullable = false)
    private String tag;

    // 유튜브 채널 URL (예: www.youtube.com/@seungjun123)
    @Column(nullable = false)
    private String channelUrl;

    // 티어 등급 (예: silver, gold, ...)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeLevel badgeLevel;

    @Builder
    public BadgeMatch(String tag, String channelUrl, BadgeLevel badgeLevel) {
        this.tag = tag;
        this.channelUrl = channelUrl;
        this.badgeLevel = badgeLevel;
    }
}
