package com.veribadge.veribadge.repository;

import com.veribadge.veribadge.domain.BadgeMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeMatchRepository extends JpaRepository<BadgeMatch, Long> {

    // tag + channelUrl이 모두 일치하는 경우에만 인정
    Optional<BadgeMatch> findByTagAndChannelUrl(String tag, String channelUrl);
}