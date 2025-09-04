package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.domain.enums.BadgeLevel;
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
import java.util.Random;

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

    public void connectChannel(String channelUrl, String email, Long userId){
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Verification verification = verificationRepository.findByUserId(member)
                .orElseThrow(() -> new CustomException(ErrorStatus.VERIFICATION_NOT_FOUND));

        Badge badge = badgeRepository.findByVerificationId(verification)
                .orElseThrow(() -> new CustomException(ErrorStatus.BADGE_NOT_FOUND));

        BadgeLevel badgeLevel = badge.getBadgeLevel();

        // Todo : 이미 채널 연결되어있으면 에러처리 필요

        String badgeTag;
        do {
            badgeTag = switch (badgeLevel) {
                case SILVER -> "@veri-silver-" + RandomStringGenerator();
                case GOLD -> "@veri-gold-" + RandomStringGenerator();
                case PLATINUM -> "@veri-platinum-" + RandomStringGenerator();
                case DIAMOND -> "@veri-diamond-" + RandomStringGenerator();
                case DOCTOR -> "@veri-doctor-" + RandomStringGenerator();
            };
        } while (badgeRepository.existsByVerifiedTag(badgeTag));

        badge.connect(channelUrl, badgeTag, email);
        badgeRepository.save(badge);

    }

    public String RandomStringGenerator() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
