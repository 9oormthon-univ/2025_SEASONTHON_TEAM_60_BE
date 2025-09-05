package com.veribadge.veribadge.mock;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.domain.enums.VerificationStatus;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class VerificationInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final VerificationRepository verificationRepository;

    @Override
    public void run(String... args){
        log.info("테스트 인증 데이터 생성");

        List<Verification> verificationsToSave = new ArrayList<>();

        // 1. just signed up

        // 2. submit certificate
        String testEmail2 = "test2@example.com";
        Member member2 = memberRepository.findByUserId(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        if (verificationRepository.findByUserId(member2).isEmpty()) {
            Verification verification2 = new Verification(
                    member2,
                    "certificate url 2"
            ); verificationsToSave.add(verification2);
        } else {
            log.info("{} 인증은 이미 존재합니다.", testEmail2);
        }

        // 3. get verifiedTag
        String testEmail3 = "test3@example.com";
        Member member3 = memberRepository.findByUserId(3L)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));
        if (verificationRepository.findByUserId(member3).isEmpty()) {
            Verification verification3 = new Verification(
                    member3,
                    "certificate url 3"
            ); verificationsToSave.add(verification3);
        } else {
            log.info("{} 인증은 이미 존재합니다.", testEmail3);
        }

        if (!verificationsToSave.isEmpty()) {
            verificationRepository.saveAll(verificationsToSave);
            log.info("테스트 인증 {}개 생성 완료", verificationsToSave.size());
        } else {
            log.info("모든 테스트 인증이 이미 존재합니다.");
        }


    }


}
