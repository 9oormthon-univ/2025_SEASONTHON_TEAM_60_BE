package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.domain.enums.VerificationStatus;
import com.veribadge.veribadge.dto.MyBadgeResponseDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.BadgeRepository;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyBadgeService {

    private final MemberRepository memberRepository;
    private final VerificationRepository verificationRepository;
    private final BadgeRepository badgeRepository;

    public MyBadgeResponseDto getMyBadge(Long userId){
        Member member = memberRepository.findByUserId(userId) // FIXME : 로그인 구현 후 수정 예정
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Optional<Verification> verification = verificationRepository.findByUserId(member);

        if (verification.isEmpty()) { // 로그인만 되어있는 사용자 (제출X, 인증X)
            return new MyBadgeResponseDto(
                    member.getUsername(),
                    member.getEmail(),
                    VerificationStatus.NOT_SUBMITTED
            );
        }

        Optional<Badge> badge = badgeRepository.findByVerificationId(verification.get());

        return badge.map(value ->
                // 인증서 제출은 했지만 아직 인증 안된 사용자 (제출O, 인증O) -> 채널 연결 O / X
                new MyBadgeResponseDto(
                        member.getUsername(),
                        member.getEmail(),
                        verification.get().getStatus(),
                        value.getBadgeLevel(),
                        value.getVerifiedDate(),
                        badge.get().getChannelUrl(),
                        badge.get().getVerifiedTag()
                )).orElseGet(() ->
                // 인증서 제출은 했지만 아직 인증 안된 사용자 (제출O, 인증 아직 or 거절)
                new MyBadgeResponseDto(
                        member.getUsername(),
                        member.getEmail(),
                        verification.get().getStatus()
                ));
    }
}
