package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.domain.enums.BadgeLevel;
import com.veribadge.veribadge.domain.enums.Role;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.BadgeRepository;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final VerificationRepository verificationRepository;
    private final BadgeRepository badgeRepository;

    public void admitVerification(Long userId, BadgeLevel badgeLevel, String description){
        // 관리자만 수정 가능하도록
//        Member member = authService.getCurrentUser();
//
//        if (member.getRole()!= Role.ADMIN){
//            throw new CustomException(ErrorStatus.UNAUTHORIZED); // http status 변경해야함
//        }

        Member customer = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Verification verification = verificationRepository.findByUserId(customer)
                .orElseThrow(() -> new CustomException(ErrorStatus.VERIFICATION_NOT_FOUND));

        description = switch (badgeLevel) { // TODO : 등급 구간 별 설명 작성 필요
            case SILVER -> "등급 구간 설명 작성";
            case GOLD -> "등급 구간 설명 작성";
            case PLATINUM -> "등급 구간 설명 작성";
            case DIAMOND -> "등급 구간 설명 작성";
            case DOCTOR -> description + " 의사입니다.";
        } ;

        verification.admitVerification(customer, description);

        verificationRepository.save(verification);

        Badge badge = new Badge(verification, LocalDate.now(), badgeLevel);
        badgeRepository.save(badge);
    }

    public void rejectVerification(Long userId, String deniedReason){
        // 관리자만 수정 가능하도록
//        Member member = authService.getCurrentUser();
//
//        if (member.getRole()!= Role.ADMIN){
//            throw new CustomException(ErrorStatus.UNAUTHORIZED); // http status 변경해야함
//        }

        Member customer = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Verification verification = verificationRepository.findByUserId(customer)
                .orElseThrow(() -> new CustomException(ErrorStatus.VERIFICATION_NOT_FOUND));

        verification.rejectVerification(customer, deniedReason);

        verificationRepository.save(verification);
    }
}
