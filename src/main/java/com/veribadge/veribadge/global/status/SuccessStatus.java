package com.veribadge.veribadge.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    // COMMON 2XX
    SUCCESS(HttpStatus.OK, "COMMON 200", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON 201", "리소스가 성공적으로 생성되었습니다."),

    // Member
    MEMBER_CREATED(HttpStatus.CREATED, "SIGNUP 201", "회원가입 성공"),
    LOGIN_SUCCESS(HttpStatus.OK, "LOGIN 200", "로그인 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, "LOGOUT 200", "로그아웃 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}