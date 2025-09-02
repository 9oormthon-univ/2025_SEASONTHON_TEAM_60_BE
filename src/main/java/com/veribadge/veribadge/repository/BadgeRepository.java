package com.veribadge.veribadge.repository;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByVerificationId(Verification verificationId);

    // tag + channelUrl이 모두 일치하는 경우에만 인정
    Optional<Badge> findByVerifiedTagAndChannelUrl(String verifiedTag, String channelUrl);
}
