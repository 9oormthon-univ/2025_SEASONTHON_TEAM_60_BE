package com.veribadge.veribadge.mock;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.enums.Role;
import com.veribadge.veribadge.repository.MemberRepository;
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
@Order(1)
@RequiredArgsConstructor
public class MemberInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;

    @Override
    public void run(String... args){
        log.info("테스트 사용자 데이터 생성");

        List<Member> membersToSave = new ArrayList<>();

        // 1. just signed up
        String testEmail1 = "test1@example.com";
        if (memberRepository.findByEmail(testEmail1).isEmpty()) {
            Member member1 = new Member(
                    1L,
                    testEmail1,
                    "김지원",
                    Role.USER,
                    LocalDateTime.now()
            ); membersToSave.add(member1);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail1);
        }

        // 2. submit certificate
        String testEmail2 = "test2@example.com";
        if (memberRepository.findByEmail(testEmail2).isEmpty()) {
            Member member2 = new Member(
                    2L,
                    testEmail2,
                    "이수한",
                    Role.USER,
                    LocalDateTime.now()
            ); membersToSave.add(member2);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail2);
        }

        // 3. get verifiedTag
        String testEmail3 = "test3@example.com";
        if (memberRepository.findByEmail(testEmail3).isEmpty()) {
            Member member3 = new Member(
                    3L,
                    testEmail3,
                    "박지수",
                    Role.USER,
                    LocalDateTime.now()
            ); membersToSave.add(member3);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail3);
        }

        if (!membersToSave.isEmpty()) {
            memberRepository.saveAll(membersToSave);
            log.info("테스트 사용자 {}명 생성 완료", membersToSave.size());
        } else {
            log.info("모든 테스트 사용자가 이미 존재합니다.");
        }
    }
}
