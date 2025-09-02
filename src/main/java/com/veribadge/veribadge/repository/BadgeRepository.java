package com.veribadge.veribadge.repository;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByVerificationId(Verification verificationId);
    boolean existsByVerifiedTag(String verifiedTag);
}
