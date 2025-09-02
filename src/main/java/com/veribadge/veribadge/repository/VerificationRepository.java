package com.veribadge.veribadge.repository;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Optional<Verification> findByUserId(Member userId);
}
