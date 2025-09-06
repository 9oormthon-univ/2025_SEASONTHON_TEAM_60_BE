package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.dto.MyAccountResponseDto;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "내 계정 정보 조회",
            description = "현재 로그인한 사용자의 기본 프로필(username)과 유튜브 채널 URL(channelUrl)을 반환합니다."
    )
    @GetMapping("/me")
    public Response<MyAccountResponseDto> getMe() {
        MyAccountResponseDto dto = accountService.getMe();
        return Response.success(SuccessStatus.USER_ME_SUCCESS, dto);
    }
}
