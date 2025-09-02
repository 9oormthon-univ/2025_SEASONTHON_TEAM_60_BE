package com.veribadge.veribadge.controller;

import ch.qos.logback.core.model.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.dto.KakaoUserInfoDto;
import com.veribadge.veribadge.dto.LoginResponseDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor

@RestController
@Slf4j
public class AuthController {
    private final KakaoService kakaoService;
    private final MemberRepository memberRepository;


    @GetMapping("/auth/kakao/callback")
    public Response<LoginResponseDto> kakaoCallback(@RequestParam String code) {

        String tokenJsonResponse = kakaoService.getAccessToken(code);
        String accessToken = kakaoService.getAccessTokenOnly(tokenJsonResponse);


        KakaoUserInfoDto userInfo = kakaoService.getUserInfo(accessToken);
        Long kakaoId = userInfo.getId();


        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    // 신규 회원일 경우 자동 가입
                    String username = userInfo.getKakao_account().getName();
                    log.info("신규 회원입니다. 이름 '{}'(으)로 자동 가입합니다.", username);
                    return memberRepository.save(Member.builder()
                            .kakaoId(kakaoId)
                            .username(username)
                            .build());
                });


        forceLogin(member);


        LoginResponseDto responseDto = new LoginResponseDto(member);
        return Response.success(SuccessStatus.LOGIN_SUCCESS, responseDto);
    }

    private void forceLogin(Member member) {
        UserDetails userDetails = new User(member.getKakaoId().toString(), "", Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
