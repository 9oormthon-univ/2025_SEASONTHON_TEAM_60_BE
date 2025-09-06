package com.veribadge.veribadge.mock;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class VerificationInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final VerificationRepository verificationRepository;

    @Override
    public void run(String... args) {
        log.info("테스트 인증 데이터 생성");

        List<Verification> verificationsToSave = new ArrayList<>();

        // 2. submit certificate (userId = 2)
        Member member2 = memberRepository.findByUserId(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        // @OneToOne 이므로 한 유저당 1건만 허용 → 이미 있으면 생성 X
        if (verificationRepository.findByUserId(member2).isEmpty()) {
            String fileId2 = "file_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String fileName2 = "income_certificate_2.png"; // 목업 파일명
            String certificateUrl2 = null; // 파일 저장 안 하므로 null 또는 "temp://" 사용 가능

            Verification v2 = new Verification(
                    member2,
                    fileId2,
                    fileName2,
                    certificateUrl2
            );
            verificationsToSave.add(v2);
        } else {
            log.info("userId=2 인증은 이미 존재합니다.");
        }

        // 3. get verifiedTag (userId = 3)
        Member member3 = memberRepository.findByUserId(3L)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        if (verificationRepository.findByUserId(member3).isEmpty()) {
            String fileId3 = "file_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String fileName3 = "income_certificate_3.jpg";
            String certificateUrl3 = null;

            Verification v3 = new Verification(
                    member3,
                    fileId3,
                    fileName3,
                    certificateUrl3
            );
            verificationsToSave.add(v3);
        } else {
            log.info("userId=3 인증은 이미 존재합니다.");
        }

        if (!verificationsToSave.isEmpty()) {
            verificationRepository.saveAll(verificationsToSave);
            log.info("테스트 인증 {}개 생성 완료", verificationsToSave.size());
        } else {
            log.info("모든 테스트 인증이 이미 존재합니다.");
        }
    }
}
