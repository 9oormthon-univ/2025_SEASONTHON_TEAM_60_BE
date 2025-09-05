package com.veribadge.veribadge.mock;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.enums.Role;
import com.veribadge.veribadge.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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

        // 1. 테스트 사용자 1
        String testEmail1 = "test1@example.com";
        if (memberRepository.findByUserId(1L).isEmpty()) {
            Member member1 = Member.builder()
                    .kakaoId(1L)
                    .username("김지원")
                    .role(Role.USER)
                    .build();
            membersToSave.add(member1);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail1);
        }

        // 2. 테스트 사용자 2
        String testEmail2 = "test2@example.com";
        if (memberRepository.findByUserId(2L).isEmpty()) {
            Member member2 = Member.builder()
                    .kakaoId(2L)
                    .username("이수한")
                    .role(Role.USER)
                    .build();
            membersToSave.add(member2);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail2);
        }

        // 3. 테스트 사용자 3
        String testEmail3 = "test3@example.com";
        if (memberRepository.findByUserId(3L).isEmpty()) {
            Member member3 = Member.builder()
                    .kakaoId(3L)
                    .username("박지수")
                    .role(Role.USER)
                    .build();
            membersToSave.add(member3);
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