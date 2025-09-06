package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Badge;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.dto.MyAccountResponseDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.BadgeRepository;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository; // 사용자 정보(username)
    private final BadgeRepository badgeRepository;   // 채널 URL 제공
    private final VerificationRepository verificationRepository;
    private final AuthService authService;;

    public MyAccountResponseDto getMe() {
        Member member = authService.getCurrentUser();

        //Member member = memberRepository.findById(3L)
        //        .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Verification verification = verificationRepository.findByUserId(member)
                .orElseThrow(() -> new CustomException(ErrorStatus.VERIFICATION_NOT_FOUND));

        Badge badge = badgeRepository.findByVerificationId(verification)
                .orElseThrow(() -> new CustomException(ErrorStatus.BADGE_NOT_FOUND));

        String channelUrl = badge.getChannelUrl();


        return new MyAccountResponseDto(member.getUsername(), channelUrl);
    }
}
