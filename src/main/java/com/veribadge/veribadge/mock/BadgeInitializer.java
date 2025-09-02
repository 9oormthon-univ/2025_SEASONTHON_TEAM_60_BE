package com.veribadge.veribadge.mock;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.domain.enums.BadgeLevel;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.BadgeRepository;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class BadgeInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final VerificationRepository verificationRepository;
    private final BadgeRepository badgeRepository;

    @Override
    public void run(String... args){
        log.info("테스트 인증 데이터 생성");

        List<Badge> badgesToSave = new ArrayList<>();

        // 1. just signed up

        // 2. submit certificate

        // 3. get verifiedTag
        String testEmail3 = "test3@example.com";
        Member member3 = memberRepository.findByEmail(testEmail3)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Verification verification3 = verificationRepository.findByUserId(member3)
                .orElseThrow(() -> new CustomException(ErrorStatus.VERIFICATION_NOT_FOUND));

        if (badgeRepository.findByVerificationId(verification3).isEmpty()) {
            Badge badge3 = new Badge(
                    verification3,
                    LocalDate.now(),
                    BadgeLevel.SILVER,
                    "Channel url 3",
                    "@veri-silver-dhlske"
            ); badgesToSave.add(badge3);
        } else {
            log.info("{} 뱃지는 이미 존재합니다.", testEmail3);
        }

        if (!badgesToSave.isEmpty()) {
            badgeRepository.saveAll(badgesToSave);
            log.info("테스트 뱃지 {}개 생성 완료", badgesToSave.size());
        } else {
            log.info("모든 테스트 뱃지는 이미 존재합니다.");
        }
    }
}
