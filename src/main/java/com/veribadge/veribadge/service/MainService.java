package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.domain.enums.VerificationStatus;
import com.veribadge.veribadge.dto.DashboardResponseDto;
import com.veribadge.veribadge.repository.BadgeRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainService {

    private final VerificationRepository verificationRepository;
    private final BadgeRepository badgeRepository;
    private final AuthService authService;

    public DashboardResponseDto getMyBadge() {
        Member member = authService.getCurrentUser();
        // Member member = memberRepository.findByUserId(userId)
        //         .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Optional<Verification> verification = verificationRepository.findByUserId(member);

        if (verification.isEmpty()) { // 로그인만 되어있는 사용자 (제출X, 인증X)
            return new DashboardResponseDto(
                    member.getUsername(),
                    member.getEmail(),
                    member.getRole(),
                    VerificationStatus.NOT_SUBMITTED
            );
        }

        Optional<Badge> badge = badgeRepository.findByVerificationId(verification.get());

        return badge.map(value ->
                // 인증서 제출은 했지만 아직 인증 안된 사용자 (제출O, 인증O)
                new DashboardResponseDto(
                    member.getUsername(),
                    member.getEmail(),
                    member.getRole(),
                    verification.get().getStatus(),
                    value.getBadgeLevel(),
                    value.getVerifiedDate()
        )).orElseGet(() ->
                // 인증서 제출은 했지만 아직 인증 안된 사용자 (제출O, 인증 아직 or 거절)
                new DashboardResponseDto(
                    member.getUsername(),
                    member.getEmail(),
                    member.getRole(),
                    verification.get().getStatus()
        ));
    }
}
